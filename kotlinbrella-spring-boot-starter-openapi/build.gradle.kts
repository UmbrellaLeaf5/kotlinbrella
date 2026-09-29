description = "Springdoc OpenAPI documentation for Kotlinbrella errors"

dependencies {
  api(project(":kotlinbrella-spring-boot-starter-webmvc"))
  api("org.springdoc:springdoc-openapi-starter-webmvc-ui:${rootProject.extra["springdocVersion"]}")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
}
