package uk.co.kiteframe.habitpal

interface Habits {
    fun save(habit: Habit)
    fun findById(id: HabitId): Habit?
    fun findAll(): List<Habit>
}