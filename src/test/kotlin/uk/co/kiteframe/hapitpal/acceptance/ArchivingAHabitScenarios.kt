package uk.co.kiteframe.hapitpal.acceptance

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import uk.co.kiteframe.habitpal.shouldHaveOneEntryMatching

class ArchivingAHabitScenarios {
    @Scenario
    fun `archiving a habit`() = habitScenario {
        startHabit(id = "ID1", name = "journal")
        startHabit(id = "ID2", name = "do 10 sit ups")

        archiveHabitOfId("ID1")

        viewHabits().shouldHaveOneEntryMatching { it.id shouldBe "ID2" }
    }

    @Scenario
    fun `archiving a non existent habit is silently ignored`() = habitScenario {
        archiveHabitOfId("dabd929c-7b7a-4b1d-be31-eb03e7caf146")

        viewHabits().shouldBeEmpty()
    }

    @Scenario
    fun `archiving a habit is handled in an idempotent fashion`() = habitScenario {
        startHabit(id = "ID1", name = "journal")

        archiveHabitOfId("ID1")
        archiveHabitOfId("ID1")

        viewHabits().shouldBeEmpty()
    }
}
