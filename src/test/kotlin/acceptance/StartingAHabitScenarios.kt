package uk.co.kiteframe.habitpal.acceptance

import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import uk.co.kiteframe.habitpal.HabitApplication
import uk.co.kiteframe.habitpal.HabitModel
import uk.co.kiteframe.habitpal.HabitType
import uk.co.kiteframe.habitpal.InMemoryHabits
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class StartingAHabitScenarios {
    @Scenario
    fun `starting a daily habit`() = habitScenario(
        clock = fixedClock("2024-05-03T10:15:30Z")
    ) {
        startHabit(
            name = "journal",
            HabitType.DAILY
        )

        viewHabits().shouldBeSingleton { habit ->
            habit.name shouldBe "journal"
            habit.type shouldBe HabitType.DAILY
            habit.startedOn shouldBe LocalDate.of(2024, 5, 3)
        }
    }

    private fun fixedClock(zonedDateTime: String) =
        Clock.fixed(Instant.parse(zonedDateTime), ZoneId.of("UTC"))

    private fun habitScenario(clock: Clock = Clock.systemUTC(), steps: InteractionMode.() -> Unit): DynamicTest {
        val directInteractionMode = DirectInteractionMode(HabitApplication(clock, InMemoryHabits()))

        return DynamicTest.dynamicTest("direct") {
            steps(directInteractionMode)
        }
    }
}

interface InteractionMode : AutoCloseable {
    fun startHabit(name: String, type: HabitType)
    fun viewHabits(): List<HabitModel>
    override fun close() = Unit
}

class DirectInteractionMode(val app: HabitApplication) : InteractionMode {
    override fun startHabit(name: String, type: HabitType) {
        app.startHabit(UUID.randomUUID().toString(), name, type)
    }

    override fun viewHabits(): List<HabitModel> = app.viewHabits()
}

@TestFactory
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Scenario
