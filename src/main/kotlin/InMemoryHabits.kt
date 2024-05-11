package uk.co.kiteframe.habitpal

class InMemoryHabits : Habits {
    private val habits: MutableList<Habit> = mutableListOf()

    override fun findAll(): List<Habit> = habits

    override fun save(habit: Habit) {
        habits.add(habit)
    }
}