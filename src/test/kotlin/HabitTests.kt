package uk.co.kiteframe.habitpal

import java.time.LocalDate
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class HabitTests {
    private val someHabitId = HabitId(UUID.randomUUID())
    private val someDate = LocalDate.of(2024, 3, 4)

    @Test
    fun `starting a habit`() {
        val habit = Habit(someHabitId, "journal", Daily, someDate)
        assertEquals(someHabitId, habit.id)
        assertEquals("journal", habit.name)
        assertEquals(Daily, habit.type)
        assertEquals(someDate, habit.startedOn)
    }
}
