package uk.co.kiteframe.habitpal

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import uk.co.kiteframe.habitpal.HabitType.DAILY
import java.time.LocalDate
import java.util.*

fun startHabit(id: String, name: String, habitType: HabitType, startedOn: LocalDate): Either<StartHabitError, Habit> {
    val habitId = HabitId(id) ?: return IdIsNotAUuid.left()
    val habitName = NonBlankString(name) ?: return BlankName.left()
    return when (habitType) {
        DAILY -> {
            Habit(habitId, habitName, Daily, startedOn).right()
        }
    }
}

enum class HabitType {
    DAILY
}

class Habit(val id: HabitId, val name: NonBlankString, val type: HabitTypeConfiguration, val startedOn: LocalDate)

sealed interface StartHabitError
data object IdIsNotAUuid : StartHabitError
data object BlankName : StartHabitError

sealed interface HabitTypeConfiguration
data object Daily : HabitTypeConfiguration

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