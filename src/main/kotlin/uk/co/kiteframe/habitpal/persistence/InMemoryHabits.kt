package uk.co.kiteframe.habitpal.persistence

import uk.co.kiteframe.habitpal.Habit
import uk.co.kiteframe.habitpal.HabitId

class InMemoryHabits : Habits {
    private val habits: MutableMap<HabitId, Habit> = mutableMapOf()

    override fun save(habit: Habit) {
        habits[habit.id] = habit
    }

    override fun findById(id: HabitId): Habit? = habits[id]

    override fun findAll(): List<Habit> = habits.values.filter { habit -> !habit.isArchived }
}