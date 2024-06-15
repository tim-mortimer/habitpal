package uk.co.kiteframe.habitpal

import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId

class HabitApplication(private val clock: Clock, private val habits: Habits) {
    fun startHabit(id: HabitId, name: NonBlankString, habitType: HabitType) {
        val startedOn = clock.instant().atZone(ZoneId.of("Europe/London")).toLocalDate()

        val habit = when (habitType) {
            HabitType.DAILY -> {
                execute(StartDailyHabit(id, name), startedOn)
            }

            HabitType.MULTIPLE_TIMES_A_DAY -> TODO()
        }

        habits.save(habit)
    }

    fun viewHabits(): List<HabitModel> =
        habits.findAll().map { habit -> HabitModel(habit.name.toString(), habit.type.toViewType(), habit.startedOn) }
}

private fun HabitTypeConfiguration.toViewType(): HabitType {
    return when (this) {
        Daily -> HabitType.DAILY
        is MultipleTimesADay -> HabitType.MULTIPLE_TIMES_A_DAY
    }
}

data class HabitModel(val name: String, val type: HabitType, val startedOn: LocalDate)
