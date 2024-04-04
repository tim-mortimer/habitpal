package uk.co.kiteframe.habitpal

import java.time.LocalDate
import java.util.*

class Habit(val id: HabitId, val name: String, val type: HabitType, val startedOn: LocalDate) {
    constructor(id: String, name: String, type: HabitType, startedOn: LocalDate) : this(
        HabitId(UUID.fromString(id)),
        name,
        type,
        startedOn
    )
}

sealed interface HabitType
data object Daily : HabitType

@JvmInline
value class HabitId(val value: UUID)