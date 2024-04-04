package uk.co.kiteframe.habitpal

import java.time.LocalDate
import java.util.*

class Habit(val id: HabitId, val name: NonBlankString, val type: HabitType, val startedOn: LocalDate)

sealed interface HabitType
data object Daily : HabitType

@JvmInline
value class HabitId(val value: UUID)

@JvmInline
value class NonBlankString(val value: String) {
    init {
        require(value.trim().isNotEmpty()) { "Habit name cannot be blank" }
    }
}