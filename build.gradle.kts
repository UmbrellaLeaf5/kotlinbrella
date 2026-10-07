import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.gradle.jvm.tasks.Jar
import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
  kotlin("jvm") version "2.3.10" apply false
  kotlin("plugin.jpa") version "2.3.10" apply false
  kotlin("plugin.spring") version "2.3.10" apply false
  id("io.spring.dependency-management") version "1.1.7" apply false
  id("org.springframework.boot") version "4.0.3" apply false
  // Dokka реально применяется к корню (агрегация документации), поэтому без apply false.
  id("org.jetbrains.dokka") version "2.2.0"
}

extra["bootBom"] = SpringBootPlugin.BOM_COORDINATES
extra["springdocVersion"] = "3.0.2"
extra["testcontainersVersion"] = "1.21.4"

allprojects {
  repositories {
    mavenCentral()
  }
}

subprojects {
  val bootBom = rootProject.extra["bootBom"] as String

  apply(plugin = "org.jetbrains.kotlin.jvm")
  apply(plugin = "java-library")
  apply(plugin = "maven-publish")

  extensions.configure<JavaPluginExtension> {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    withSourcesJar()
    if (!project.path.startsWith(":samples")) withJavadocJar()
  }

  extensions.configure<KotlinJvmProjectExtension> {
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_21)
      freeCompilerArgs.add("-Xjsr305=strict")
    }
  }

  dependencies {
    add(
      "testImplementation",
      platform(bootBom),
    )
    add("testImplementation", "org.junit.jupiter:junit-jupiter")
    add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")
  }

  tasks.withType<Test>().configureEach {
    useJUnitPlatform()
  }

  tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
  }

  if (!project.path.startsWith(":samples")) {
    apply(plugin = "org.jetbrains.dokka")

    val dokkaPublicationHtml = tasks.named("dokkaGeneratePublicationHtml")

    tasks.named<Jar>("javadocJar") {
      dependsOn(dokkaPublicationHtml)
      from(dokkaPublicationHtml.map { it.outputs.files })
    }

    tasks.named("javadoc") { enabled = false }

    extensions.configure<PublishingExtension> {
      publications {
        create<MavenPublication>("mavenJava") {
          from(components["java"])
          pom {
            name.set(project.name)
            description.set(project.description)
            url.set("https://github.com/UmbrellaLeaf5/kotlinbrella")
            licenses {
              license {
                name.set("The Unlicense")
                url.set("https://unlicense.org/")
              }
            }
          }
        }
      }
    }
  }
}

dependencies {
  dokka(project(":kotlinbrella-core"))
  dokka(project(":kotlinbrella-spring-boot-autoconfigure"))
  dokka(project(":kotlinbrella-spring-boot-starter-webmvc"))
  dokka(project(":kotlinbrella-spring-boot-starter-validation"))
  dokka(project(":kotlinbrella-spring-boot-starter-data-jpa"))
  dokka(project(":kotlinbrella-spring-boot-starter-openapi"))
  dokka(project(":kotlinbrella-spring-boot-starter-access"))
  dokka(project(":kotlinbrella-spring-boot-starter"))
}
