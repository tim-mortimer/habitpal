package uk.co.kiteframe.habitpal

interface Habits {
    fun findAll(): List<Habit>
    fun save(habit: Habit)
}