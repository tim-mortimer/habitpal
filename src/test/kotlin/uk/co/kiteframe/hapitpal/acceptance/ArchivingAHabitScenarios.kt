package uk.co.kiteframe.hapitpal.acceptance

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import uk.co.kiteframe.habitpal.shouldHaveOneEntryMatching

class ArchivingAHabitScenarios {
    @Scenario
    fun `archiving a habit`() = habitScenario {
        val firstHabit = startHabit(name = "journal")
        val secondHabit = startHabit(name = "do 10 sit ups")

        archiveHabitOfId(firstHabit.id)

        viewHabits().shouldHaveOneEntryMatching { habit -> habit shouldBe secondHabit }
    }

    @Scenario
    fun `archiving a non existent habit is silently ignored`() = habitScenario {
        archiveHabitOfId("dabd929c-7b7a-4b1d-be31-eb03e7caf146")

        viewHabits().shouldBeEmpty()
    }

    @Scenario
    fun `archiving a habit is handled in an idempotent fashion`() = habitScenario {
        val firstHabit = startHabit(name = "journal")

        archiveHabitOfId(firstHabit.id)
        archiveHabitOfId(firstHabit.id)

        viewHabits().shouldBeEmpty()
    }
}
