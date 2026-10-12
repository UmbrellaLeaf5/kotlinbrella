# Kotlinbrella Implementation Plan

План реализации и архитектурный журнал Kotlinbrella. Фазы выполнять по
порядку, если только поздний пункт явно не зависит от стабильного публичного
контракта из ранней фазы. Не публиковать артефакт, чей публичный API не покрыт
unit- и интеграционными тестами.

Выполненное удаляется, а не помечается: чего нет в списке — то отгружено.

## Phase 1: Future Extensions

Будущие расширения, не входящие в текущий объём:

- [ ] Интеграция со Spring Security на основе аутентифицированных principal,
      никаких ID из тел запросов.
- [ ] Расширения метрик, трейсинга и структурированного логирования.
- [ ] KSP — только исследование и только после того, как два приложения разделят
      стабильный контракт «интерфейс-делегат»; не начинать с рефлексивного
      кодогенератора.

## Phase 2: Закалка OpenAPI-слоя (независимое ревью, 2026-10)

Причина: ядро зрелое, OpenAPI-слой нет. Ревью двух потребителей (`root:dev`,
`slicer-api:dev`) показало одни и те же боли: кастомайзер неидемпотентен,
`SIMPLE`-шейп теряет `code`, опечатка в `code` роняет всю спеку, конкуренцию
оба сервиса пишут руками. Каждый пункт ниже — с тестами, без новых
конфигурационных ключей, кроме явно оговорённого `forceErrorSchema`.

- [ ] Step 2.1 — идемпотентный `mergeDescription`.
      Зачем: `springdoc` вызывает `customize()` многократно, сейчас описание
      обрастает дублями `• detail<br>• detail`.
      Что править: `KotlinbrellaErrorOperationCustomizer.kt:mergeDescription`.
      Как:

      ```kotlin
      // было:
      response.description = if (response.description.isNullOrBlank()) bullets
      else response.description + DESCRIPTION_SEPARATOR + bullets

      // стало: добавляем только свежие пули
      private fun mergeDescription(response: ApiResponse, errors: List<ApiError>) {
        val fresh = errors
          .filter { error -> response.description?.contains(error.detail) != true }
          .joinToString(Constants.ApiSpec.DESCRIPTION_SEPARATOR) {
            Constants.ApiSpec.BULLET_PREFIX + it.detail
          }

        if (fresh.isEmpty()) return

        response.description = if (response.description.isNullOrBlank()) fresh
        else response.description + Constants.ApiSpec.DESCRIPTION_SEPARATOR + fresh
      }
      ```

      Покрыть тестом: двойной `customize()` одной операции — описание не меняется.

- [ ] Step 2.2 — всегда `examples`, никогда `example` + `examples` вместе.
      Зачем: при одной ошибке пишется `media.example`, при нескольких —
      `media.examples`, противоположное поле не чистится. Swagger UI рендерит
      неоднозначно.
      Что править: `renderSimple`/`renderStandard` в том же файле.
      Как: унифицировать на `examples` даже для одиночки; при записи
      проставлять `media.example = null`:

      ```kotlin
      // стало (фрагмент renderSimple):
      media.example = null

      for (error in errors) {
        val example = Example()
        example.summary = error.detail
        example.value = mapOf(
          Constants.Web.ERROR_KEY to error.title,
          Constants.ApiSpec.MESSAGE_KEY to error.detail,
        )

        media.addExamples(exampleNameFor(error, media), example)
      }
      ```

      Покрыть тестом: `media.example == null` при любом числе ошибок.

- [ ] Step 2.3 — ключ example по `code`, а не по свободному тексту.
      Зачем: сейчас SIMPLE-ключ — `error.detail` (текст с пробелами,
      коллизии при одинаковом `detail` у разных `code`); в STANDARD индекс
      `${code}-${i}` нестабилен при перестановке аннотаций.
      Что править: тот же файл, обе ветки рендера.
      Как:

      ```kotlin
      private fun exampleNameFor(error: ApiError, media: MediaType): String {
        var name = error.code
        var index = 2

        while (media.examples?.containsKey(name) == true) {
          val kept = media.examples[name]?.value as? Map<*, *>

          if (kept?.get(Constants.ApiSpec.MESSAGE_KEY) == error.detail) return name

          name = "${error.code}-$index"
          index++
        }

        return name
      }
      ```

      Покрыть тестом: два `code` с одинаковым `detail` дают `CODE` и `CODE-2`;
      повторный прогон ключи не меняет.

- [ ] Step 2.4 — fail-fast валидация `code` при старте вместо `throw` в спеке.
      Зачем: одна опечатка в `@ApiError(code = "...")` сейчас роняет весь
      `/v3/api-docs` в рантайме (`HttpStatus.resolve ?: throw`,
      `ErrorCode.status/title else -> throw`).
      Что править: новый бин-валидатор в `autoconfigure` (рядом с
      `KotlinbrellaOpenApiAutoConfiguration`, под тем же
      `@ConditionalOnWebApplication`), плюс `warn`-ветка в самом кастомайзере.
      Как:

      ```kotlin
      @Component
      @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
      class ApiErrorCodeValidator(
        private val mappings: RequestMappingHandlerMapping,
      ) {

        // MARK: Fail fast on unknown error codes
        // --------------------------------------------------

        /**
         * Checks every [ApiErrors] code at startup so a typo
         * cannot break spec generation at runtime.
         */
        @EventListener(ApplicationReadyEvent::class)
        fun validate() {
          for ((_, handler) in mappings.handlerMethods) {
            val errors = AnnotatedElementUtils.findMergedAnnotation(
              handler.method,
              ApiErrors::class.java,
            )?.value ?: continue

            for (error in errors) {
              try {
                ErrorCode.status(error.code)
              } catch (e: IllegalArgumentException) {
                throw IllegalStateException(
                  "Unknown ApiError code '${error.code}' on ${handler.method}", e
                )
              }
            }
          }
        }
      }
      ```

      В `customize()` при неизвестном коде — пропуск с `warn`, не `throw`.
      Покрыть тестом: контекст с битым `code` не стартует.

- [ ] Step 2.5 — `KotlinbrellaSchemaCustomizer`: чистить чужой шейп при SIMPLE.
      Зачем: при `errorShape == SIMPLE` сейчас `addSchemas(SimpleErrorResponse);
    return` — если `Problem`/`Violation` уже зарегистрированы, в
      `components/schemas` остаётся мусор после переключения шейпа.
      Что править: `KotlinbrellaSchemaCustomizer.kt:customise`.
      Как:

      ```kotlin
      if (properties.errorShape == ErrorShape.SIMPLE) {
        components.schemas?.remove(Constants.ApiSpec.PROBLEM_SCHEMA)
        components.schemas?.remove(Constants.ApiSpec.VIOLATION_SCHEMA)

        if (components.schemas?.containsKey(Constants.ApiSpec.SIMPLE_SCHEMA) != true)
          components.addSchemas(Constants.ApiSpec.SIMPLE_SCHEMA, simpleSchema())

        return
      }
      ```

      Покрыть тестом: после `customise` при SIMPLE в схемах только
      `SimpleErrorResponse`.

- [ ] Step 2.6 — опция `forceErrorSchema` (единственный новый ключ фазы).
      Зачем: сейчас чужая схема на ошибочном статусе молча остаётся, а пример
      пишется под неё — спека врёт.
      Что править: `KotlinbrellaWebProperties` + обе ветки рендера.
      Как:

      ```kotlin
      // KotlinbrellaWebProperties:
      var forceErrorSchema: Boolean = false
      ```

      ```kotlin
      // в renderSimple/renderStandard перед установкой своей схемы:
      val expectedRef = ... // SIMPLE_REFERENCE или PROBLEM_REFERENCE

      if (media.schema != null && media.schema.`$ref` != expectedRef) {
        logger.warn("Error response keeps foreign schema {}, expected {}", media.schema.`$ref`, expectedRef)

        if (properties.forceErrorSchema)
          media.schema = Schema<Any>().`$ref`(expectedRef)
      }
      ```

      По умолчанию `false` — поведение не меняется, только `warn` в логе.
      Покрыть тестом обе ветки.

- [ ] Step 2.7 — утилиты конкуренции из `slicer-api` (без XA/брокера).
      Зачем: оба сервиса пишут advisory-локи руками; ключ
      `listingId.mostSignificantBits.toInt()` с неочевидным усечением UUID
      сидит в прикладном коде.
      Что править: чистый `UUID`-extension — в `core` (там нет Spring);
      JDBC-хелпер и SQL-константа — в `autoconfigure`.
      Как:

      ```kotlin
      // core/.../util/AdvisoryLockUtils.kt (чистый Kotlin, без Spring):
      /**
       * Derives a 32-bit advisory-lock key from a UUID.
       * Collisions are acceptable for transaction-scoped locks.
       */
      fun UUID.toAdvisoryLockKey(): Int = mostSignificantBits.toInt()
      ```

      ```kotlin
      // autoconfigure Constants.kt:
      object Sql {
        const val TRY_ADVISORY_XACT_LOCK = "SELECT pg_try_advisory_xact_lock(?)"
      }

      // autoconfigure/.../support/JdbcLockUtils.kt:
      /**
       * Tries a transaction-scoped advisory lock, auto-released
       * on commit or rollback.
       */
      fun pgTryAdvisoryXactLock(jdbcTemplate: JdbcTemplate, key: Int): Boolean =
        jdbcTemplate.queryForObject(
          Constants.Sql.TRY_ADVISORY_XACT_LOCK, Boolean::class.java, key
        ) == true
      ```

      Плюс KDoc-рецепт `insertPendingIfAbsent` (дока, не стартер).
      Покрыть unit-тестами на Testcontainers/H2? Только моки `JdbcTemplate` —
      без новой инфраструктуры.

- [ ] Step 2.8 — `BiDeleteVerifier` для кросс-БД удалений.
      Зачем: прямая боль `slicer-api` (`tasksDeleted != calculationsDeleted`
      лишь логируется, остаются висячие задачи для воркера).
      Что править: новый хелпер в `core/.../util/`.
      Как:

      ```kotlin
      /**
       * Fails a two-database delete when the second side
       * removed fewer rows than expected, so the caller
       * retries the idempotent batch instead of leaving orphans.
       */
      fun verifyBiDelete(expected: Int, actual: Int, target: String) {
        if (expected == actual) return

        throw ConflictException.unified(
          "Partial delete of $target: expected $expected, removed $actual, please retry"
        )
      }
      ```

      Покрыть unit-тестами (ровно/меньше/ноль).

- [ ] Step 2.9 — batch-аннотации поверх существующего `@ValidUUID`.
      Зачем: оба сервиса валидируют батчи руками; `List<@ValidUUID String>`
      работает, но лимит размера и непустоту каждый пишет заново.
      Что править: новая композитная аннотация в `validation`.
      Как:

      ```kotlin
      @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
      @Retention(AnnotationRetention.RUNTIME)
      @NotNull
      @Size(min = 1, max = 100)
      @Constraint(validatedBy = [])
      annotation class BatchIds(
        val message: String = "Batch must contain 1 to 100 ids",
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = [],
      )
      ```

      Использование (элементы по-прежнему проверяет `@ValidUUID`):

      ```kotlin
      fun deleteCalculations(
        @RequestBody @BatchIds idStrings: List<@ValidUUID String>
      ): ResponseEntity<CalculationDeleteBatchResponse>
      ```

      Лимит — параметром аннотации, не константой сервиса. Покрыть
      валидационными тестами (пусто/ровно/больше).

- [ ] Step 2.10 — тест-контракт паритета «спека == рантайм».
      Зачем: отдать потребителям готовый тест вместо Python-сниппетов.
      Что править: новый модуль `kotlinbrella-test` с одним объектом.
      Как:

      ```kotlin
      object ErrorDocsParity {

        /**
         * Asserts the documented error example matches
         * the real MVC response body field by field.
         */
        fun assertParity(
          mockMvc: MockMvc,
          openApiJson: String,
          method: String,
          path: String,
          probeUrl: String,
        ) {
          // 1. example = openApiJson.paths[path][method].responses[code]
          //    .content[media].example (или examples.first())
          // 2. body = mockMvc.perform(get(probeUrl)).andReturn().response.contentAsString
          // 3. assertEquals по каждому ключу example
        }
      }
      ```

      Использование в потребителе (пример для `simple`):

      ```kotlin
      @Test
      fun errorDocsMatchRuntime() {
        val spec = mockMvc.perform(get("/v3/api-docs"))
          .andReturn().response.contentAsString

        ErrorDocsParity.assertParity(
          mockMvc = mockMvc,
          openApiJson = spec,
          method = "get",
          path = "/api/v1/calculation/{id}/status",
          probeUrl = "/api/v1/calculation/00000000-0000-0000-0000-000000000000/status",
        )
      }
      ```

      Покрыть матрицей `simple/standard × dev/prod` на фикстурах модуля.

## Phase 3: Documentation as One System (GUIDE.md + Pages, no duplication)

Принцип: у каждого типа знаний ровно один дом. `GUIDE.md` — нарратив
(зачем, когда, как настраивать, рецепты, миграционные заметки).
Сайт Pages (Dokka) — справочник (что: пакеты, классы, функции).
`GUIDE.md` ссылается в Dokka-страницы; KDoc ссылается наружу в разделы
`GUIDE.md`, где поведению нужен нарратив. Проза между ними не дублируется.

- [ ] Step 3.1 — курирование Dokka: `package.md` на публичный пакет, скрыть
      пакеты `*internal*` из навигации, README-инклюды на агрегатной странице,
      футер с версией тега плюс ссылкой на GitHub, `suppressObviousFunctions`
      и добор KDoc для непокрытого публичного API.
- [ ] Step 3.2 — `GUIDE.md` по use-case'ам (ошибки в обеих формах, валидация,
      доступ, JPA, OpenAPI, конверсии, примеры) со ссылками в Dokka-справочник;
      ужа
      ...[truncated 10858 chars]
