package uk.co.kiteframe.habitpal

import java.time.LocalDate
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class HabitTests {
    private val someUuidString = UUID.randomUUID().toString()
    private val someDate = LocalDate.of(2024, 3, 4)

    @Test
    fun `starting a habit`() {
        val habit = Habit(someUuidString, "journal", Daily, someDate)
        assertEquals(HabitId(UUID.fromString(someUuidString)), habit.id)
        assertEquals("journal", habit.name)
        assertEquals(Daily, habit.type)
        assertEquals(someDate, habit.startedOn)
    }
}
