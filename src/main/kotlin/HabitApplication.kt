package uk.co.kiteframe.habitpal

import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId

class HabitApplication(private val clock: Clock, private val habits: Habits) {
    fun startDailyHabit(id: HabitId, name: NonBlankString): Habit {
        return execute(StartDailyHabit(id, name), dateNow()).also { habits.save(it) }
    }

    fun startMultipleTimesADayHabit(id: HabitId, name: NonBlankString, multiple: Multiple): Habit {
        return execute(StartMultipleTimesADayHabit(id, name, multiple), dateNow()).also { habits.save(it) }
    }

    private fun dateNow(): LocalDate = clock.instant().atZone(ZoneId.of("Europe/London")).toLocalDate()

    fun viewHabits(): List<HabitModel> =
        habits.findAll().map { habit ->
            HabitModel(
                habit.name.toString(),
                habit.type.toViewType(),
                habit.type.toViewTimes(),
                habit.startedOn
            )
        }
}

private fun HabitTypeConfiguration.toViewType(): HabitType {
    return when (this) {
        Daily -> HabitType.DAILY
        is MultipleTimesADay -> HabitType.MULTIPLE_TIMES_A_DAY
    }
}

private fun HabitTypeConfiguration.toViewTimes(): Int? {
    return when (this) {
        Daily -> null
        is MultipleTimesADay -> multiple.value
    }
}

data class HabitModel(val name: String, val type: HabitType, val times: Int?, val startedOn: LocalDate)
