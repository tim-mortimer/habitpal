package uk.co.kiteframe.habitpal

import org.junit.jupiter.api.assertThrows
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

    @Test
    fun `cannot start a habit with an invalid UUID`() {
        assertThrows<IllegalArgumentException> {
            startHabit("blah", someName, Daily, someDate)
        }
    }

    @Test
    fun `cannot start a habit with a blank name`() {
        assertThrows<IllegalArgumentException> {
            startHabit(someUuid, "", Daily, someDate)
        }
        assertThrows<IllegalArgumentException> {
            startHabit(someUuid, " ", Daily, someDate)
        }
    }
}
