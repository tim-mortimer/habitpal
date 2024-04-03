package domain

import domain.HabitCommand.PerformHabit
import domain.HabitCommand.StartHabit
import domain.HabitEvent.HabitStarted
import java.time.LocalDate
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class HabitTests {
    private val randomUUID = UUID.randomUUID()

    @Test
    fun `starting a habit`() {
        val habit: Habit = execute(
            StartHabit(HabitId(randomUUID), NonEmptyString("Journal"), HabitType.DAILY),
        )

        assertEquals(
            listOf(
                HabitStarted(
                    HabitId(randomUUID), NonEmptyString("Journal"), HabitType.DAILY
                )
            ),
            habit
        )
    }

    @Test
    fun `performing a habit`() {
        val habit: Habit = listOf(
            HabitStarted(
                HabitId(randomUUID), NonEmptyString("Journal"), HabitType.DAILY
            )
        )

        val performedHabit = execute(PerformHabit(HabitId(randomUUID), LocalDate.of(2024, 4, 3)), habit)

        assertEquals(
            listOf(
                HabitStarted(
                    HabitId(randomUUID), NonEmptyString("Journal"), HabitType.DAILY
                ),
                HabitEvent.HabitPerformed(
                    HabitId(randomUUID), LocalDate.of(2024, 4, 3)
                )
            ),
            performedHabit
        )
    }
}

