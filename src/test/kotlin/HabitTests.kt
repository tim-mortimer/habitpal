package uk.co.kiteframe.habitpal

import java.time.LocalDate
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class HabitTests {
    private val someUuid = "f47ac10b-58cc-4372-a567-0e02b2c3d479"
    private val someName = "journal"
    private val someDate = LocalDate.of(2024, 3, 4)

    @Test
    fun `starting a habit`() {
        val habit: Habit = startHabit(someUuid, someName, Daily, someDate)
        assertEquals(HabitId(UUID.fromString(someUuid)), habit.id)
        assertEquals("journal", habit.name.toString())
        assertEquals(Daily, habit.type)
        assertEquals(someDate, habit.startedOn)
    }
}
