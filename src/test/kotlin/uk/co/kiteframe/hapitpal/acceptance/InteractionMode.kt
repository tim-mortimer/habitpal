package uk.co.kiteframe.hapitpal.acceptance

import uk.co.kiteframe.habitpal.*
import java.util.*

sealed interface InteractionMode : AutoCloseable {
    fun startHabit(
        id: String = UUID.randomUUID().toString(),
        name: String,
        type: HabitType = HabitType.DAILY,
        times: Int? = null
    ): HabitModel

    fun archiveHabitOfId(id: String)
    fun viewHabits(): List<HabitModel>
    override fun close() = Unit
}

class DirectInteractionMode(private val app: HabitApplication, private val context: TestContext) : InteractionMode {
    override fun startHabit(id: String, name: String, type: HabitType, times: Int?): HabitModel {
        val habitId = HabitId(context.aliasHabitIdFor(id))!!
        return when (type) {
            HabitType.DAILY -> app.startDailyHabit(habitId, NonBlankString(name)!!)
            HabitType.MULTIPLE_TIMES_A_DAY -> app.startMultipleTimesADayHabit(
                habitId,
                NonBlankString(name)!!,
                Multiple(times!!)!!
            )
        }
    }

    override fun archiveHabitOfId(id: String) = app.archiveHabit(HabitId(UUID.fromString(context.aliasHabitIdFor(id))))

    override fun viewHabits(): List<HabitModel> =
        app.viewHabits().map { it.copy(id = context.scenarioHabitIdFor(it.id) ?: "") }
}