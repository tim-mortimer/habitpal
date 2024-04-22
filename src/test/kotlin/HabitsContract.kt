package uk.co.kiteframe.habitpal

import java.time.LocalDate
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

abstract class HabitsContract(private val habits: Habits) {

    @Test
    fun `returns no habits when none are saved`() {
        assertEquals(listOf(), habits.findAll())
    }

    @Test
    fun `returns habits when one is saved`() {
        val aHabit = Habit(HabitId(UUID.randomUUID()), NonBlankString("wash up")!!, Daily, LocalDate.now())
        habits.save(aHabit)
        assertEquals(listOf(aHabit), habits.findAll())
    }
}

class InMemoryHabitsTests : HabitsContract(InMemoryHabits())
