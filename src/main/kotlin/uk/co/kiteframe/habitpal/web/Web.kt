package uk.co.kiteframe.habitpal.web

import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.core.*
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.Method.GET
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.CONFLICT
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.SEE_OTHER
import org.http4k.core.Status.Companion.UNPROCESSABLE_ENTITY
import org.http4k.lens.*
import org.http4k.routing.*
import org.http4k.routing.ResourceLoader.Companion.Classpath
import org.http4k.routing.ResourceLoader.Companion.Directory
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.TemplateRenderer
import org.http4k.template.ViewModel
import org.http4k.template.viewModel
import uk.co.kiteframe.habitpal.*
import uk.co.kiteframe.habitpal.configuring.toDbConfig
import uk.co.kiteframe.habitpal.configuring.toDslContext
import uk.co.kiteframe.habitpal.persistence.DbHabits
import uk.co.kiteframe.habitpal.persistence.Habits
import uk.co.kiteframe.habitpal.persistence.InMemoryHabits
import java.time.Clock
import java.util.*

private fun environment() = Environment.JVM_PROPERTIES overrides
        Environment.ENV overrides
        Environment.from(
            "env" to "production",
            "jdbc.url" to "jdbc:postgresql://localhost:5432/habitpal",
            "db.username" to "habitpal",
            "db.password" to "habitpal"
        )

fun main() {
    val environment = environment()
    val dbConfig = environment.toDbConfig()

    application(
        DbHabits(dbConfig.toDslContext()),
        templatesFor(environment)
    )
        .asServer(Undertow(8000))
        .start()
}

private fun templatesFor(environment: Environment): TemplateRenderer {
    val env = EnvironmentKey.defaulted("env", "production")(environment)

    return when {
        env == "production" -> HandlebarsTemplates().CachingClasspath()
        else -> HandlebarsTemplates().HotReload("src/main/resources")
    }
}

private fun staticResourceRouterFor(environment: Environment): RoutingHttpHandler {
    val env = EnvironmentKey.defaulted("env", "production")(environment)

    val resourceLoader = when {
        env == "production" -> Classpath("/static")
        else -> Directory("src/main/resources/static")
    }

    return static(resourceLoader)
}

fun application(
    habits: Habits = InMemoryHabits(),
    renderer: TemplateRenderer = HandlebarsTemplates().CachingClasspath()
): HttpHandler {
    val application = HabitApplication(Clock.systemUTC(), habits)
    val view = Body.viewModel(renderer, TEXT_HTML).toLens()

    return routes(
        htmxWebjars(),
        staticResourceRouterFor(environment()),
        Request.isHtmx bind idempotencyFilter.then(
            routes(
                "/habits" bind Method.POST to startHabit(application)
            )
        ),
        "/" bind GET to { Response(SEE_OTHER).header("Location", "/habits") },
        "/habits" bind routes(
            GET to viewHabits(view, application)
        )
    )
}

val idempotencyFilter =
    Filter { next ->
        handler@{ request: Request ->
            val key = request.header("Idempotency-Key")
            when {
                key.isNullOrBlank() -> Response(BAD_REQUEST)
                request.hasInvalidIdempotencyKey() -> Response(BAD_REQUEST)
                idempotencyKeys.hasDifferingPayloadForKey(key, request) -> Response(UNPROCESSABLE_ENTITY)
                idempotencyKeys.hasInFlightRequestFor(key) -> Response(CONFLICT)
                else -> {
                    val cachedResponse = idempotencyKeys.completedResponseFor(key)
                    if (cachedResponse != null) return@handler cachedResponse
                    idempotencyKeys.lock(key, request)
                    val response = next(request)
                    idempotencyKeys.release(key, response)
                    response
                }
            }
        }
    }


private fun Request.hasInvalidIdempotencyKey(): Boolean {
    try {
        UUID.fromString(header("Idempotency-Key"))
        return false
    } catch (e: IllegalArgumentException) {
        return true
    }
}

interface IdempotencyKeys {
    fun hasDifferingPayloadForKey(key: String, request: Request): Boolean
    fun hasInFlightRequestFor(key: String): Boolean
    fun completedResponseFor(key: String): Response?
    fun lock(key: String, request: Request)
    fun release(key: String, response: Response)
}

val idempotencyKeys =
    object : IdempotencyKeys {
        private val keys: MutableMap<String, Pair<Request, Response?>> = mutableMapOf()

        override fun hasDifferingPayloadForKey(key: String, request: Request) =
            keys.containsKey(key) && keys[key]?.first != request

        override fun hasInFlightRequestFor(key: String) =
            keys.containsKey(key) && keys[key]?.second == null

        override fun completedResponseFor(key: String): Response? = keys[key]?.second

        override fun lock(key: String, request: Request) {
            keys[key] = request to null
        }

        override fun release(key: String, response: Response) {
            val pair = keys[key]
            if (pair != null) {
                keys[key] = pair.first to response
            }
        }
    }

private fun startHabit(application: HabitApplication) = { request: Request ->
    val nameField = FormField.nonBlankString().required("name")
    val typeField = FormField.enum<HabitType>().required("type")
    val timesField = FormField.int().map { Multiple(it) }.optional("times")
    val feedbackForm = Body.webForm(Validator.Feedback, nameField, typeField, timesField).toLens()
    val submitted = feedbackForm(request)
    if (submitted.errors.any()) {
        Response(BAD_REQUEST).body(submitted.errors.toString())
    } else {
        val id = HabitId(UUID.randomUUID())
        val name = nameField(submitted)
        val type = typeField(submitted)

        when (type) {
            HabitType.DAILY -> {
                application.startDailyHabit(id, name)
                Response(SEE_OTHER).header("Location", "/habits")
            }

            HabitType.MULTIPLE_TIMES_A_DAY -> {
                val times: Multiple? = timesField(submitted)
                if (times == null) {
                    Response(BAD_REQUEST).body("[formData 'times' must be >= 2]")
                } else {
                    application.startMultipleTimesADayHabit(
                        id,
                        name,
                        times
                    )
                    Response(SEE_OTHER).header("Location", "/habits")
                }
            }
        }
    }
}

private fun FormField.nonBlankString() = map(
    BiDiMapping<String, NonBlankString>(
        { NonBlankString(it) ?: throw IllegalArgumentException("String cannot be blank") },
        { it.toString() })
)

private fun viewHabits(
    view: BiDiBodyLens<ViewModel>,
    application: HabitApplication
): (request: Request) -> Response =
    { Response(OK).with(view of ViewHabits(application.viewHabits())) }

data class ViewHabits(val habits: List<HabitModel>) : ViewModel