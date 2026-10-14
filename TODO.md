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

## Phase 2: Добор ревью-2 (мелочь с конкретными находками)

По итогам второго независимого ревью (0.3.2, оба сервиса подняты). Всё ниже —
маленькие пункты с точным местом правки; каждый — с тестом.

- [ ] Step 2.1 — точное сравнение пуль и симметричная чистка шейпов.
      Зачем: `mergeDescription` ищет `contains(detail)` — подстрочный
      (`"problem"` съест `"First problem"`); при смене шейпа остаётся чужой
      media-type, а `SIMPLE→STANDARD` не удаляет `SimpleErrorResponse`.
      Что править: `KotlinbrellaErrorOperationCustomizer.kt` (`mergeDescription`,
      `renderSimple`/`renderStandard`), `KotlinbrellaSchemaCustomizer.kt`.
      Как:

      ```kotlin
      // было:
      .filter { error -> response.description?.contains(error.detail) != true }

      // стало:
      .filter { error ->
        response.description?.split(Constants.ApiSpec.DESCRIPTION_SEPARATOR)
          ?.none { it == Constants.ApiSpec.BULLET_PREFIX + error.detail } != false
      }
      ```

      В `renderSimple` удалять `application/problem+json`-медиа при наличии,
      в `renderStandard` — наоборот; в схеме-стороне при STANDARD удалять
      `SimpleErrorResponse` симметрично SIMPLE-ветке. Покрыть тестами обе
      стороны (шейп туда-обратно на одной спеке).

- [ ] Step 2.2 — `ErrorDocsParity`: пробы по методу, строгие типы, лишние поля.
      Зачем: сейчас пробы всегда `GET` (параметр `method` используется только
      для поиска в спеке), `asText()` склеивает `404` и `"404"`, лишние
      runtime-поля не ловятся.
      Что править: `kotlinbrella-test/.../ErrorDocsParity.kt`.
      Как: построить запрос по `method` (`get/post/delete/...` из
      `MockMvcRequestBuilders`), сравнивать поля строгим JSON-равенством
      (`value.toString() == body.path(key).toString()` вместо `asText()`),
      добавить параметр `strict: Boolean = true` — при `true` падать на полях
      body, которых нет в примере. Покрыть тестами: POST-проба, mismatch типов,
      лишнее поле при `strict` и молчание при `strict = false`.

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
      ужать `README.md` до титульной страницы со ссылками.
      Зафиксировать здесь политику библиотеки (`ApiException` для рантайм-ошибок
      клиентов; `IllegalStateException`/`IllegalArgumentException` для ошибок
      конфигурации), а также решения из ревью-2, которым не нужен код:
      `ApiErrorCodeValidator` смотрит только MVC-маппинг (actuator вне скоупа);
      `ValidBatchIds` — только required-непустой батч (опциональный — отдельным
      способом); дефолт `forceErrorSchema = false` (keep+warn) достаточен,
      включать принудительно только с причиной.
- [ ] Step 3.3 — проверить локальным `:dokkaGenerate` и визуально главную
      страницу плюс два модуля; деплой едет следующим тегом.

## Phase 4: Снос разделения dev/prod-сообщений (приказ руководства, breaking)

Разделения на скрытые и открытые сообщения больше нет: никакого legacy,
никаких `@Deprecated`-алиасов, чистый снос везде. Единое сообщение — бывший
диагностический (информативный) текст: это то, что сегодня видит dev-профиль
и что ассертят Python autotests обоих сервисов. Выполнять до Phase 3 (доки
описывают уже новый контракт); релиз — новой версией с миграционной заметкой,
после — подъём root и slicer-api (их этапы в их TODO).

- [ ] Step 4.1 — схлопнуть `ApiException`: одно сообщение вместо пары.
      Зачем: `publicDetail`/`diagnosticDetail` и `(devMessage, prodMessage)`
      во всех 7 сабклассах (`BadRequest/NotFound/Conflict/Forbidden/
      InternalServer/PayloadTooLarge/ServiceUnavailableException`) больше не
      нужны; `unified()` как отдельная фабрика теряет смысл.
      Что править: `kotlinbrella-core/.../error/ApiException.kt` (одно поле,
      `RuntimeException(message)`), 7 сабклассов — конструктор
      `(message: String, code: String = ..., cause: Throwable? = null)`,
      `unified()` удалить, не депрекейтить.
      Как:

      ```kotlin
      // было:
      throw ConflictException(devMessage = "...", prodMessage = "...")
      throw ConflictException.unified("...")

      // стало:
      throw ConflictException("...")
      ```

- [ ] Step 4.2 — схлопнуть библиотечные генераторы сообщений.
      Зачем: `toUUIDOrThrow`/`toInstantOrThrow` (`.../util/StringConversions.kt`)
      и `findByIdOrThrow`
      (`starter-data-jpa/.../data/RepositoryExtensions.kt`) бросают
      двухтекстовые исключения (`"Invalid UUID: <value>"` vs `"Invalid UUID"`,
      `"... not found with id: <id>"` vs `"... not found"`).
      Что править: те же файлы — оставить только информативный (бывший dev)
      текст; вторую константу (`INVALID_UUID`, `notFound`) удалить, если
      больше нигде не используется.
- [ ] Step 4.3 — удалить `mode` из автоконфигурации.
      Зачем: `KotlinbrellaWebProperties.mode`, `enum/Mode.kt`,
      `exposeDiagnostics()`/`verbose()` и 4 тернарника в
      `KotlinbrellaErrorAdvice.kt` (`:61` выбор detail исключения, `:142`
      unreadable, `:162` mismatch, `:190` missing) — мёртвый код после сноса;
      фреймворковые хендлеры всегда отдают детализированный текст.
      Что править: удалить `Mode.kt`, поле `mode`, тернарники (оставить
      dev-ветку как единственную); `KotlinbrellaJpaErrorAdvice.kt`
      («Hide constraint and lock diagnostics») — раскрыть детали тоже.
      Исчезновение ключа `kotlinbrella.web.mode` — в миграционную заметку.
- [ ] Step 4.4 — переписать тесты на единый текст.
      Что править: `ApiExceptionTest` (ассёрты пары detail'ов),
      `KotlinbrellaErrorAdviceTest` (`redactsDiagnosticDetailsByDefault`,
      `devModeEnablesDiagnosticDetails`,
      `diagnosticDetailsNameFieldsWithoutRejectedValues` — удалить/переписать
      на единый текст), `KotlinbrellaJpaErrorAdviceTest`,
      `RepositoryExtensionsTest`, `DataJpaIntegrationTest` (упоминает mode).
- [ ] Step 4.5 — релиз breaking-версии и миграционная заметка.
      В release notes: удалены `mode`, `diagnosticDetail`/`publicDetail`,
      `unified()`, двухаргументные конструкторы; `kotlinbrella.web.mode` из
      yml удалить; единый текст = бывший dev. Дальше — подъём root и
      slicer-api.

      Готово, когда: `grep -rn "diagnosticDetail\|publicDetail\|exposeDiagnostics\|fun unified\|devMessage\|prodMessage\|enum class Mode" --include="*.kt" */src/main` пуст; `mode` нет в пропертях; build зелёный.

## Не делать

- [ ] Не расширять `SIMPLE {error, message}` новыми полями — только мажорной
      версией: фронт обоих потребителей парсит ровно два поля.
- [ ] Не тащить XA/брокер/распределённые транзакции — преждевременно, решение
      принято; `verifyBiDelete + 409 + retry` достаточно.
- [ ] Не генерировать мапперы (MapStruct/KSP) — рукописные мапперы в корне
      домена соблюдаются в обоих сервисах и работают; кодоген убьёт конвенцию.
- [ ] Не требовать Kotlin-тесты в сервисах — регрессия Python autotests,
      решение принято; вместо этого давать parity-сниппеты для Python.
- [ ] Не тащить BiDelete-сверки в библиотеку — двух-БД семантика (что с чем
      сверять, какой retry допустим) принадлежит сервисам. `verifyBiDelete`
      по ошибке попал в `0.3.2`, удалён из `main`; потребителям его не
      использовать — сверку писать локально в сервисе (образец — Р2 TODO
      slicer-api).

## Release Checklist (recurring, run on every release)

- [ ] Проверить совместимость публичного API и описать миграцию.
- [ ] Подтвердить: все рантайм-ошибки клиентов — семейство `ApiException`.
- [ ] Прогнать build, интеграционные тесты, матрицу совместимости и сэмплы.
- [ ] Проверить: сгенерированный OpenAPI совпадает с MVC-ответами.
- [ ] Проверить граф зависимостей: `core` без фреймворков, у точечных
      стартеров нет случайных зависимостей.
- [ ] Просканировать исходники, тесты, доки и артефакты сборки на креды,
      токены, локальные пути и dev-URL.
- [ ] Сгенерировать release notes с поддерживаемыми версиями и изменениями
      конфигурации.
- [ ] Поставить тег, чтобы JitPack собрал версионные артефакты с sources и
      javadoc; документация деплоится через `docs.yml`.

