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

    @Test
    fun `archived habits are not returned`() {
        val archivedHabit =
            Habit(HabitId(UUID.randomUUID()), NonBlankString("wash up")!!, Daily, LocalDate.now())
                .archive()

        val anotherHabit = Habit(
            HabitId(UUID.randomUUID()),
            NonBlankString("dry up")!!,
            MultipleTimesADay(Multiple(2)!!),
            LocalDate.now().minusDays(3)
        )

        habits.save(archivedHabit)
        habits.save(anotherHabit)

        assertEquals(listOf(anotherHabit), habits.findAll())
    }

    @Test
    fun `can find a habit by id`() {
        val id = HabitId(UUID.randomUUID())
        val aHabit = Habit(id, NonBlankString("wash up")!!, Daily, LocalDate.now())
        val anotherHabit = Habit(
            HabitId(UUID.randomUUID()),
            NonBlankString("dry up")!!,
            MultipleTimesADay(Multiple(2)!!),
            LocalDate.now().minusDays(3)
        )
        habits.save(aHabit)
        habits.save(anotherHabit)
        assertEquals(aHabit, habits.findById(id))
    }

    @Test
    fun `habits are saved in an idempotent fashion`() {
        val aHabit = Habit(HabitId(UUID.randomUUID()), NonBlankString("wash up")!!, Daily, LocalDate.now())
        habits.save(aHabit)
        assertEquals(1, habits.findAll().size)

        habits.save(aHabit)
        assertEquals(1, habits.findAll().size)
    }
}

class InMemoryHabitsTests : HabitsContract(InMemoryHabits())
