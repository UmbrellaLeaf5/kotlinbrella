package io.github.umbrellaleaf5.kotlinbrella.data

import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootConfiguration
@EnableAutoConfiguration
@EntityScan("io.github.umbrellaleaf5.kotlinbrella.data.storage")
@EnableJpaRepositories("io.github.umbrellaleaf5.kotlinbrella.data.storage")
class JpaTestApplication
