package domain

import domain.HabitCommand.StartHabit
import domain.HabitEvent.HabitStarted
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class HabitTests {
    @Test
    fun `starting a habit`() {
        val randomUUID = UUID.randomUUID()

        val habit: Habit = execute(
            StartHabit(HabitId(randomUUID), NonEmptyString("Journal"), HabitType.DAILY)
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
}

