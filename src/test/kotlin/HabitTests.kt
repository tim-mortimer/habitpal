package uk.co.kiteframe.habitpal

import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HabitTests {
    private val someUuid = "f47ac10b-58cc-4372-a567-0e02b2c3d479"
    private val someName = "journal"
    private val someHabitType = HabitType.DAILY
    private val someDate = LocalDate.of(2024, 3, 4)

    @Test
    fun `starting a habit`() {
        val habit = startHabit(someUuid, someName, someHabitType, someDate)
        assertTrue(habit.isRight())
        habit.getOrNone().map {
            with(it) {
                assertEquals(HabitId(UUID.fromString(someUuid)), id)
                assertEquals("journal", name.toString())
                assertEquals(Daily, type)
                assertEquals(someDate, startedOn)
            }
        }
    }

    @Test
    fun `cannot start a habit with an invalid UUID`() {
        val result = startHabit("blah", someName, HabitType.DAILY, someDate)
        assertEquals(IdIsNotAUuid, result.leftOrNull())
    }

    @Test
    fun `cannot start a habit with a blank name`() {
        assertThrows<IllegalArgumentException> {
            startHabit(someUuid, "", HabitType.DAILY, someDate)
        }
        assertThrows<IllegalArgumentException> {
            startHabit(someUuid, " ", HabitType.DAILY, someDate)
        }
    }
}
