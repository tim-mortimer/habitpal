package uk.co.kiteframe.habitpal.uk.co.kiteframe.hapitpal.acceptance

import io.kotest.matchers.shouldBe
import uk.co.kiteframe.habitpal.uk.co.kiteframe.habitpal.HabitType
import uk.co.kiteframe.habitpal.uk.co.kiteframe.habitpal.shouldHaveOneEntryMatching
import java.time.LocalDate

class StartingAHabitScenarios {
    @Scenario
    fun `starting a daily habit`() = habitScenario(
        clock = fixedClock("2024-05-03T10:15:30Z")
    ) {
        startHabit(
            name = "journal",
            HabitType.DAILY
        )

        viewHabits().shouldHaveOneEntryMatching { habit ->
            habit.name shouldBe "journal"
            habit.type shouldBe HabitType.DAILY
            habit.startedOn shouldBe LocalDate.of(2024, 5, 3)
        }
    }

    @Scenario
    fun `starting a habit performed multiple times a day`() = habitScenario(
        clock = fixedClock("2024-05-03T10:15:30Z")
    ) {
        startHabit(
            name = "do ten press ups",
            type = HabitType.MULTIPLE_TIMES_A_DAY,
            times = 2
        )

        viewHabits().shouldHaveOneEntryMatching { habit ->
            habit.name shouldBe "do ten press ups"
            habit.type shouldBe HabitType.MULTIPLE_TIMES_A_DAY
            habit.times shouldBe 2
            habit.startedOn shouldBe LocalDate.of(2024, 5, 3)
        }
    }

    @Scenario
    fun `habit names are trimmed`() = habitScenario {
        startHabit(name = " journal ")

        viewHabits().shouldHaveOneEntryMatching { habit ->
            habit.name shouldBe "journal"
        }
    }
}
