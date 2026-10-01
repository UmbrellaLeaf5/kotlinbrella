description = "Springdoc OpenAPI documentation for Kotlinbrella errors"

val springdocVersion = rootProject.extra["springdocVersion"]

dependencies {
  api(project(":kotlinbrella-spring-boot-starter-webmvc"))
  api("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
}
