package uk.co.kiteframe.habitpal

import java.time.LocalDate
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class WebTest {
    private val someUuid = "f47ac10b-58cc-4372-a567-0e02b2c3d479"
    private val someName = "journal"
    private val someHabitType = HabitType.DAILY
    private val someDate = LocalDate.of(2024, 3, 4)

    @Test
    fun `starting a daily habit`() {
        startHabit(someUuid, someName, someHabitType, someDate)
            .should {
                assertEquals(
                    Habit(HabitId(UUID.fromString(someUuid)), NonBlankString("journal")!!, Daily, someDate),
                    this
                )
            }
    }

    @Test
    fun `starting a habit performed multiples time a day`() {
        startHabit(someUuid, someName, HabitType.MULTIPLE_TIMES_A_DAY, someDate, times = 2)
            .should {
                assertEquals(MultipleTimesADay(Multiple(2)!!), type)
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
    fun `cannot start a multiple times a day habit without multiplicity`() {
        startHabit(someUuid, someName, HabitType.MULTIPLE_TIMES_A_DAY, someDate, times = 0)
            .shouldFailWith(NoMultiplicity)

        startHabit(someUuid, someName, HabitType.MULTIPLE_TIMES_A_DAY, someDate, times = 1)
            .shouldFailWith(NoMultiplicity)

        startHabit(someUuid, someName, HabitType.MULTIPLE_TIMES_A_DAY, someDate, times = -5)
            .shouldFailWith(NoMultiplicity)

        startHabit(someUuid, someName, HabitType.MULTIPLE_TIMES_A_DAY, someDate, times = null)
            .shouldFailWith(NoMultiplicity)
    }
}