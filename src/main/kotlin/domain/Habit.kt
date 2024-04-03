package domain

import domain.HabitCommand.StartHabit
import java.util.*

enum class HabitType {
    DAILY
}

fun execute(command: HabitCommand): Habit {
    return when (command) {
        is StartHabit -> listOf(HabitEvent.HabitStarted(command.id, command.name, command.type))
    }
}

sealed interface HabitCommand {
    val id: HabitId

    data class StartHabit(override val id: HabitId, val name: NonEmptyString, val type: HabitType) : HabitCommand
}

@JvmInline
value class HabitId(val value: UUID)
typealias Habit = List<HabitEvent>

sealed interface HabitEvent {
    val id: HabitId

    data class HabitStarted(override val id: HabitId, val name: NonEmptyString, val type: HabitType) : HabitEvent
}

@JvmInline
value class NonEmptyString(val value: String)