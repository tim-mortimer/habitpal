package uk.co.kiteframe.habitpal.uk.co.kiteframe.habitpal

class InMemoryHabits : Habits {
    private val habits: MutableMap<HabitId, Habit> = mutableMapOf()

    override fun save(habit: Habit) {
        habits[habit.id] = habit
    }

    override fun findById(id: HabitId): Habit? = habits[id]

    override fun findAll(): List<Habit> = habits.values.filter { habit -> !habit.isArchived }
}