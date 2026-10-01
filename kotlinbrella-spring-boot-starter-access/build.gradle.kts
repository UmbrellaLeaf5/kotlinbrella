description = "Spring AOP access and ownership checks"

val bootBom = rootProject.extra["bootBom"] as String

dependencies {
  api(project(":kotlinbrella-spring-boot-autoconfigure"))
  api(platform(bootBom))
  api("org.springframework.boot:spring-boot-starter-aspectj")
  api("org.jetbrains.kotlin:kotlin-reflect")
}
