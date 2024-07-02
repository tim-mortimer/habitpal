package uk.co.kiteframe.habitpal

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.http4k.core.*
import org.http4k.format.Jackson.auto
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Undertow
import org.http4k.server.asServer
import java.time.Clock

fun main() {
    application().asServer(Undertow(8000)).start()
}

fun application(): HttpHandler {
    val requestLens = Body.auto<StartHabitRequest>().toLens()
    val application = HabitApplication(Clock.systemUTC(), InMemoryHabits())
    return routes(
        "/habits" bind Method.POST to { request: Request ->
            val extractedRequest = requestLens(request)
            val result = startHabit(application, extractedRequest)
            result.toResponse()
        }
    )
}

data class StartHabitRequest(val id: String, val name: String, val habitType: HabitType, val times: Int? = null)

private fun startHabit(
    application: HabitApplication, request: StartHabitRequest,
): Either<StartHabitError, HabitModel> {
    val habitId = HabitId(request.id) ?: return IdIsNotAUuid.left()
    val habitName = NonBlankString(request.name) ?: return BlankName.left()

    when (request.habitType) {
        HabitType.DAILY -> return application.startDailyHabit(habitId, habitName).right()
        HabitType.MULTIPLE_TIMES_A_DAY -> {
            val multiple = Multiple(request.times ?: 0) ?: return NoMultiplicity.left()
            return application.startMultipleTimesADayHabit(habitId, habitName, multiple).right()
        }
    }
}

private fun Either<StartHabitError, HabitModel>.toResponse(): Response {
    return this.fold(
        { error -> Response(Status.BAD_REQUEST).body(error.toMessage()) },
        { Response(Status.OK) }
    )
}

fun StartHabitError.toMessage() = when (this) {
    BlankName -> "Name cannot be blank"
    IdIsNotAUuid -> "Provided ID is not a valid UUID"
    NoMultiplicity -> "A habit performed multiple times per day can't have a multiplicity less than two"
}

sealed interface StartHabitError
data object IdIsNotAUuid : StartHabitError
data object BlankName : StartHabitError
data object NoMultiplicity : StartHabitError