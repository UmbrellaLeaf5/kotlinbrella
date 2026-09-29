description = "Spring Data JPA repository helpers and persistence errors"

dependencies {
  api(project(":kotlinbrella-spring-boot-autoconfigure"))
  api(platform("org.springframework.boot:spring-boot-dependencies:${rootProject.extra["bootVersion"]}"))
  api("org.springframework.boot:spring-boot-starter-data-jpa")
  api("org.jetbrains.kotlin:kotlin-reflect")
  testRuntimeOnly("org.postgresql:postgresql")
  testImplementation("org.mockito:mockito-core")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.testcontainers:postgresql:${rootProject.extra["testcontainersVersion"]}")
  testImplementation("org.testcontainers:junit-jupiter:${rootProject.extra["testcontainersVersion"]}")
}
