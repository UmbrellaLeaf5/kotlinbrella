description = "Framework-free Kotlin utilities and client error contracts"

val bootBom = rootProject.extra["bootBom"] as String

dependencies {
  api(platform(bootBom))
  api("com.fasterxml.jackson.core:jackson-annotations")
}
