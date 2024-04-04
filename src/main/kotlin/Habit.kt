package uk.co.kiteframe.habitpal

import java.time.LocalDate
import java.util.*

fun startHabit(id: String, name: String, habitType: HabitType, startedOn: LocalDate): Habit {
    return Habit(HabitId.of(id), NonBlankString(name), habitType, startedOn)
}

class Habit(val id: HabitId, val name: NonBlankString, val type: HabitType, val startedOn: LocalDate)

sealed interface HabitType
data object Daily : HabitType

@JvmInline
value class HabitId(val value: UUID) {
    companion object {
        fun of(uuid: String) = HabitId(UUID.fromString(uuid))
    }
}

@JvmInline
value class NonBlankString(val value: String) {
    init {
        require(value.trim().isNotEmpty()) { "Habit name cannot be blank" }
    }

    override fun toString(): String = value
}