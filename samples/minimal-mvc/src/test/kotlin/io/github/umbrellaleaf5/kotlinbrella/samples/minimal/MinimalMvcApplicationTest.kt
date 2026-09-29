package io.github.umbrellaleaf5.kotlinbrella.samples.minimal

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.util.ClassUtils

@SpringBootTest
@AutoConfigureMockMvc
class MinimalMvcApplicationTest(
  // services:
  @param:Autowired private val mvc: MockMvc,
) {

  // MARK: Verify focused starter dependencies and runtime endpoint
  // --------------------------------------------------

  @Test
  fun usesOnlyWebDependencies() {
    assertFalse(ClassUtils.isPresent("org.springframework.data.jpa.repository.JpaRepository",
      javaClass.classLoader))
    assertFalse(ClassUtils.isPresent("org.springdoc.core.customizers.OperationCustomizer",
      javaClass.classLoader))
    assertFalse(ClassUtils.isPresent("org.aspectj.lang.annotation.Aspect", javaClass.classLoader))

    val response = mvc.perform(get("/health")).andReturn().response

    assertTrue(response.status == 200)
  }

}
