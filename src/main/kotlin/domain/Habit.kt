package domain

import domain.HabitCommand.PerformHabit
import domain.HabitCommand.StartHabit
import domain.HabitEvent.HabitPerformed
import domain.HabitEvent.HabitStarted
import java.time.LocalDate
import java.util.*

enum class HabitType {
    DAILY
}

fun execute(command: HabitCommand, habit: Habit = listOf()): Habit {
    return when (command) {
        is StartHabit -> listOf(HabitStarted(command.id, command.name, command.type))
        is PerformHabit -> habit + listOf(HabitPerformed(command.id, command.performedOn))
    }
}

sealed interface HabitCommand {
    val id: HabitId

    data class StartHabit(override val id: HabitId, val name: NonEmptyString, val type: HabitType) : HabitCommand
    data class PerformHabit(override val id: HabitId, val performedOn: LocalDate) : HabitCommand
}

@JvmInline
value class HabitId(val value: UUID)
typealias Habit = List<HabitEvent>

sealed interface HabitEvent {
    val id: HabitId

    data class HabitStarted(override val id: HabitId, val name: NonEmptyString, val type: HabitType) : HabitEvent
    data class HabitPerformed(override val id: HabitId, val performedOn: LocalDate) : HabitEvent
}

@JvmInline
value class NonEmptyString(val value: String)