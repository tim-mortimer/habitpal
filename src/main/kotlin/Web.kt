package uk.co.kiteframe.habitpal

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.http4k.core.Body
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.Jackson.auto
import org.http4k.server.Undertow
import org.http4k.server.asServer
import java.time.LocalDate

fun main() {
    val requestLens = Body.auto<StartHabitRequest>().toLens()
    val app: (Request) -> Response = { request: Request ->
        val extractedRequest = requestLens(request)
        val result = startHabit(
            extractedRequest.id,
            extractedRequest.name,
            extractedRequest.habitType,
            LocalDate.now(),
            extractedRequest.times
        )
        result.toResponse()
    }

    app.asServer(Undertow(8000)).start()
}

data class StartHabitRequest(val id: String, val name: String, val habitType: HabitType, val times: Int)

fun startHabit(
    id: String, name: String, habitType: HabitType, startedOn: LocalDate, times: Int? = null
): Either<StartHabitError, Habit> {
    val habitId = HabitId(id) ?: return IdIsNotAUuid.left()
    val habitName = NonBlankString(name) ?: return BlankName.left()

    return when (habitType) {
        HabitType.DAILY -> execute(StartDailyHabit(habitId, habitName), startedOn).right()
        HabitType.MULTIPLE_TIMES_A_DAY -> {
            times ?: return NoMultiplicity.left()
            val multiple = Multiple(times) ?: return NoMultiplicity.left()
            execute(StartMultipleTimesADayHabit(habitId, habitName, multiple), startedOn).right()
        }
    }
}

private fun Either<StartHabitError, Habit>.toResponse(): Response {
    return this.fold(
        { error -> Response(Status.BAD_REQUEST).body(error.toMessage()) },
        { Response(Status.OK) }
    )
}

private fun StartHabitError.toMessage() = when (this) {
    BlankName -> "Name cannot be blank"
    IdIsNotAUuid -> "Provided ID is not a valid UUID"
    NoMultiplicity -> "A habit performed multiple times per day can't have a multiplicity less than two"
}

sealed interface StartHabitError
data object IdIsNotAUuid : StartHabitError
data object BlankName : StartHabitError
data object NoMultiplicity : StartHabitError