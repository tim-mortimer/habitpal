package uk.co.kiteframe.habitpal.persistence

import uk.co.kiteframe.habitpal.Habit
import uk.co.kiteframe.habitpal.HabitId

interface Habits {
    fun save(habit: Habit)
    fun findById(id: HabitId): Habit?
    fun findAll(): List<Habit>
}