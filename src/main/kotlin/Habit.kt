package uk.co.kiteframe.habitpal

import uk.co.kiteframe.habitpal.HabitType.*
import java.time.LocalDate
import java.util.*

fun startHabit(id: String, name: String, habitType: HabitType, startedOn: LocalDate): Habit {
    return when (habitType) {
        DAILY -> Habit(HabitId.of(id), NonBlankString(name), Daily, startedOn)
    }
}

enum class HabitType {
    DAILY
}

class Habit(val id: HabitId, val name: NonBlankString, val type: HabitTypeConfiguration, val startedOn: LocalDate)

sealed interface HabitTypeConfiguration
data object Daily : HabitTypeConfiguration

@JvmInline
value class HabitId(val value: UUID) {
    companion object {
        fun of(uuid: String) = HabitId(UUID.fromString(uuid))
    }
}

@JvmInline
value class NonBlankString(private val value: String) {
    init {
        require(value.trim().isNotEmpty()) { "Habit name cannot be blank" }
    }

    override fun toString(): String = value
}