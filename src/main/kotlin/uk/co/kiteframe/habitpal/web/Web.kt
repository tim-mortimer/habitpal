package uk.co.kiteframe.habitpal.web

import org.http4k.core.*
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.Method.GET
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.SEE_OTHER
import org.http4k.lens.*
import org.http4k.routing.bind
import org.http4k.routing.htmxWebjars
import org.http4k.routing.routes
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.TemplateRenderer
import org.http4k.template.ViewModel
import org.http4k.template.viewModel
import uk.co.kiteframe.habitpal.*
import uk.co.kiteframe.habitpal.persistence.Habits
import uk.co.kiteframe.habitpal.persistence.InMemoryHabits
import java.time.Clock
import java.util.*

fun main() {
    application(InMemoryHabits())
        .asServer(Undertow(8000))
        .start()
}

fun application(
    habits: Habits = InMemoryHabits(),
    renderer: TemplateRenderer = HandlebarsTemplates().CachingClasspath()
): HttpHandler {
    val application = HabitApplication(Clock.systemUTC(), habits)
    val view = Body.viewModel(renderer, TEXT_HTML).toLens()

    return routes(
        htmxWebjars(),
        Request.isHtmx bind routes(
            "/habits" bind Method.POST to startHabit(application)
        ),
        "/" bind GET to { Response(SEE_OTHER).header("Location", "/habits") },
        "/habits" bind routes(
            GET to viewHabits(view, application)
        )
    )
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