package uk.co.kiteframe.habitpal

import java.util.concurrent.atomic.AtomicReference

class InMemoryHabits : Habits {
    private val habits: AtomicReference<MutableList<Habit>> = AtomicReference(mutableListOf())

    override fun findAll(): List<Habit> = habits.get()

    override fun save(habit: Habit) {
        val currentHabits = habits.get() ?: mutableListOf()
        currentHabits.add(habit)
        habits.set(currentHabits)
    }
}