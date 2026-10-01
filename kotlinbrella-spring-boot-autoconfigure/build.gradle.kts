description = "Conditional Spring Boot configurations for Kotlinbrella"

val bootBom = rootProject.extra["bootBom"] as String
val springdocVersion = rootProject.extra["springdocVersion"]

dependencies {
  api(project(":kotlinbrella-core"))
  api(platform(bootBom))
  compileOnly("org.springframework.boot:spring-boot-autoconfigure")
  compileOnly("org.springframework.boot:spring-boot-starter-webmvc")
  compileOnly("org.springframework.boot:spring-boot-starter-validation")
  compileOnly("org.springframework:spring-tx")
  compileOnly("org.springframework.boot:spring-boot-starter-aspectj")
  compileOnly("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")
  testImplementation("org.springframework.boot:spring-boot-starter-webmvc")
  testImplementation("org.springframework.boot:spring-boot-starter-validation")
  testImplementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")
  testImplementation("org.springframework.boot:spring-boot-starter-data-jpa")
  testImplementation("org.springframework.boot:spring-boot-starter-aspectj")
  testImplementation("org.jetbrains.kotlin:kotlin-reflect")
  testImplementation("org.springframework:spring-test")
  testImplementation("org.springframework.boot:spring-boot-test")
  testImplementation("org.assertj:assertj-core")
  testImplementation("ch.qos.logback:logback-classic")
  testImplementation(project(":kotlinbrella-spring-boot-starter-validation"))
}
