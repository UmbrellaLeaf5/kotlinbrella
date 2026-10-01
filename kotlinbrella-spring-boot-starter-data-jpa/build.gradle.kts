description = "Spring Data JPA repository helpers and persistence errors"

val bootBom = rootProject.extra["bootBom"] as String
val testcontainersVersion = rootProject.extra["testcontainersVersion"]

dependencies {
  api(project(":kotlinbrella-spring-boot-autoconfigure"))
  api(platform(bootBom))
  api("org.springframework.boot:spring-boot-starter-data-jpa")
  api("org.jetbrains.kotlin:kotlin-reflect")
  testRuntimeOnly("org.postgresql:postgresql")
  testImplementation("org.mockito:mockito-core")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.testcontainers:postgresql:$testcontainersVersion")
  testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
}
