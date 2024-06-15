package uk.co.kiteframe.habitpal.acceptance

import uk.co.kiteframe.habitpal.*
import java.util.*

sealed interface InteractionMode : AutoCloseable {
    fun startHabit(name: String, type: HabitType = HabitType.DAILY)
    fun viewHabits(): List<HabitModel>
    override fun close() = Unit
}

class DirectInteractionMode(val app: HabitApplication) : InteractionMode {
    override fun startHabit(name: String, type: HabitType) {
        app.startHabit(HabitId(UUID.randomUUID().toString())!!, NonBlankString(name)!!, type)
    }

    override fun viewHabits(): List<HabitModel> = app.viewHabits()
}