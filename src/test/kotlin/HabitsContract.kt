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
    fun `returns a habits when one is saved`() {
        val aHabit = Habit(HabitId(UUID.randomUUID()), NonBlankString("wash up")!!, Daily, LocalDate.now())
        habits.save(aHabit)
        assertEquals(listOf(aHabit), habits.findAll())
    }

    @Test
    fun `returns multiple habits`() {
        val aHabit = Habit(HabitId(UUID.randomUUID()), NonBlankString("wash up")!!, Daily, LocalDate.now())
        val anotherHabit = Habit(
            HabitId(UUID.randomUUID()),
            NonBlankString("dry up")!!,
            MultipleTimesADay(Multiple(2)!!),
            LocalDate.now().minusDays(3)
        )
        habits.save(aHabit)
        habits.save(anotherHabit)
        assertEquals(listOf(aHabit, anotherHabit), habits.findAll())
    }
}

class InMemoryHabitsTests : HabitsContract(InMemoryHabits())
