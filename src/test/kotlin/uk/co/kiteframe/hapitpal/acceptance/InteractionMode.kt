package uk.co.kiteframe.habitpal.uk.co.kiteframe.hapitpal.acceptance

import uk.co.kiteframe.habitpal.uk.co.kiteframe.habitpal.*
import java.util.*

sealed interface InteractionMode : AutoCloseable {
    fun startHabit(name: String, type: HabitType = HabitType.DAILY, times: Int? = null): HabitModel
    fun archiveHabitOfId(id: String)
    fun viewHabits(): List<HabitModel>
    override fun close() = Unit
}

class DirectInteractionMode(private val app: HabitApplication) : InteractionMode {
    override fun startHabit(name: String, type: HabitType, times: Int?): HabitModel {
        return when (type) {
            HabitType.DAILY -> app.startDailyHabit(HabitId(UUID.randomUUID().toString())!!, NonBlankString(name)!!)
            HabitType.MULTIPLE_TIMES_A_DAY -> app.startMultipleTimesADayHabit(
                HabitId(UUID.randomUUID().toString())!!,
                NonBlankString(name)!!,
                Multiple(times!!)!!
            )
        }
    }

    override fun archiveHabitOfId(id: String) = app.archiveHabit(HabitId(UUID.fromString(id)))

    override fun viewHabits(): List<HabitModel> = app.viewHabits()
}