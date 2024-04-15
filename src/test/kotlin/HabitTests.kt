package uk.co.kiteframe.habitpal

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import java.time.LocalDate
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class HabitTests {
    private val someUuid = "f47ac10b-58cc-4372-a567-0e02b2c3d479"
    private val someName = "journal"
    private val someHabitType = HabitType.DAILY
    private val someDate = LocalDate.of(2024, 3, 4)

    @Test
    fun `starting a habit`() {
        startHabit(someUuid, someName, someHabitType, someDate)
            .should {
                assertEquals(
                    Habit(HabitId(UUID.fromString(someUuid)), NonBlankString("journal")!!, Daily, someDate),
                    this
                )
            }
    }

    @Test
    fun `starting a habit performed multiple time a day`() {
        startHabit(someUuid, someName, HabitType.MULTIPLE_TIMES_A_DAY, someDate, times = 2)
            .should {
                assertEquals(MultipleTimesADay(2), type)
            }
    }

    @Test
    fun `cannot start a habit with an invalid UUID`() {
        startHabit("blah", someName, someHabitType, someDate).shouldFailWith(IdIsNotAUuid)
    }

    @Test
    fun `cannot start a habit with a blank name`() {
        startHabit(someUuid, "", someHabitType, someDate).shouldFailWith(BlankName)
        startHabit(someUuid, " ", someHabitType, someDate).shouldFailWith(BlankName)
    }

    @Test
    fun `habit names are trimmed`() {
        startHabit(someUuid, "journal ", someHabitType, someDate)
            .should {
                assertEquals("journal", name.toString())
            }
    }

    private fun Either<StartHabitError, Habit>.should(assertions: Habit.() -> Unit) {
        assertions.invoke(this.getOrElse { fail("Starting habit failed with ${this.left()}") })
    }

    private fun Either<StartHabitError, Habit>.shouldFailWith(error: StartHabitError) {
        assertTrue { this.isLeft() }
        assertEquals(error, this.leftOrNull())
    }
}
