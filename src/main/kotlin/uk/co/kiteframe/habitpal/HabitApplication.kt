package uk.co.kiteframe.habitpal

import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId

class HabitApplication(private val clock: Clock, private val habits: Habits) {
    fun startDailyHabit(id: HabitId, name: NonBlankString): HabitModel {
        return Habit(id, name, Daily, dateNow()).also { habits.save(it) }.toViewModel()
    }

    fun startMultipleTimesADayHabit(id: HabitId, name: NonBlankString, multiple: Multiple): HabitModel {
        return Habit(id, name, MultipleTimesADay(multiple), dateNow()).also { habits.save(it) }.toViewModel()
    }

    fun archiveHabit(id: HabitId) {
        habits.findById(id)?.archive()?.let { habits.save(it) }
    }

    fun viewHabits(): List<HabitModel> = habits.findAll().map { habit -> habit.toViewModel() }

    private fun dateNow(): LocalDate = clock.instant().atZone(ZoneId.of("Europe/London")).toLocalDate()
}

private fun Habit.toViewModel() = HabitModel(
    id.value.toString(),
    name.toString(),
    type.toViewType(),
    type.toViewTimes(),
    startedOn
)

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

data class HabitModel(val id: String, val name: String, val type: HabitType, val times: Int?, val startedOn: LocalDate)
