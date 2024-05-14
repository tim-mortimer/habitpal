package uk.co.kiteframe.habitpal

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import uk.co.kiteframe.habitpal.HabitType.DAILY
import uk.co.kiteframe.habitpal.HabitType.MULTIPLE_TIMES_A_DAY
import java.time.LocalDate
import java.util.*

fun startHabit(
    id: String, name: String, habitType: HabitType, startedOn: LocalDate, times: Int? = null
): Either<StartHabitError, Habit> {
    val habitId = HabitId(id) ?: return IdIsNotAUuid.left()
    val habitName = NonBlankString(name) ?: return BlankName.left()

    return when (habitType) {
        DAILY -> execute(StartDailyHabit(habitId, habitName), startedOn).right()
        MULTIPLE_TIMES_A_DAY -> {
            times ?: return NoMultiplicity.left()
            val multiple = Multiple(times) ?: return NoMultiplicity.left()
            execute(StartMultipleTimesADayHabit(habitId, habitName, multiple), startedOn).right()
        }
    }
}

fun execute(command: StartDailyHabit, startedOn: LocalDate) = Habit(command.id, command.name, Daily, startedOn)

fun execute(command: StartMultipleTimesADayHabit, startedOn: LocalDate) =
    Habit(command.id, command.name, MultipleTimesADay(command.multiple), startedOn)

data class StartDailyHabit(val id: HabitId, val name: NonBlankString)
data class StartMultipleTimesADayHabit(val id: HabitId, val name: NonBlankString, val multiple: Multiple)

enum class HabitType {
    DAILY, MULTIPLE_TIMES_A_DAY
}

data class Habit(val id: HabitId, val name: NonBlankString, val type: HabitTypeConfiguration, val startedOn: LocalDate)

sealed interface StartHabitError
data object IdIsNotAUuid : StartHabitError
data object BlankName : StartHabitError
data object NoMultiplicity : StartHabitError

sealed interface HabitTypeConfiguration
data object Daily : HabitTypeConfiguration
data class MultipleTimesADay(val multiple: Multiple) : HabitTypeConfiguration

@JvmInline
value class HabitId(private val value: UUID) {
    companion object {
        operator fun invoke(uuid: String): HabitId? = try {
            HabitId(UUID.fromString(uuid))
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}

@JvmInline
value class NonBlankString private constructor(private val value: String) {
    companion object {
        operator fun invoke(value: String): NonBlankString? {
            return when (val trimmedValue = value.trim()) {
                "" -> null
                else -> NonBlankString(trimmedValue)
            }
        }
    }

    override fun toString(): String {
        return value
    }
}

@JvmInline
value class Multiple private constructor(private val value: Int) {
    companion object {
        operator fun invoke(times: Int): Multiple? =
            if (times > 1) Multiple(times) else null
    }
}