package uk.co.kiteframe.habitpal

import java.time.LocalDate
import java.util.*

enum class HabitType {
    DAILY, MULTIPLE_TIMES_A_DAY
}

fun execute(command: StartDailyHabit, startedOn: LocalDate) = Habit(command.id, command.name, Daily, startedOn)

fun execute(command: StartMultipleTimesADayHabit, startedOn: LocalDate) =
    Habit(command.id, command.name, MultipleTimesADay(command.multiple), startedOn)

data class StartDailyHabit(val id: HabitId, val name: NonBlankString)
data class StartMultipleTimesADayHabit(val id: HabitId, val name: NonBlankString, val multiple: Multiple)

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
value class Multiple private constructor(val value: Int) {
    companion object {
        operator fun invoke(times: Int): Multiple? =
            if (times > 1) Multiple(times) else null
    }
}

data class Habit(val id: HabitId, val name: NonBlankString, val type: HabitTypeConfiguration, val startedOn: LocalDate)

sealed interface HabitTypeConfiguration
data object Daily : HabitTypeConfiguration
data class MultipleTimesADay(val multiple: Multiple) : HabitTypeConfiguration