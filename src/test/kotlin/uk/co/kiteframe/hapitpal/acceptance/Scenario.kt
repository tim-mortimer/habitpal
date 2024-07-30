package uk.co.kiteframe.hapitpal.acceptance

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import uk.co.kiteframe.habitpal.HabitApplication
import uk.co.kiteframe.habitpal.persistence.InMemoryHabits
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

fun habitScenario(clock: Clock = Clock.systemUTC(), steps: InteractionMode.() -> Unit): DynamicTest {
    val directInteractionMode = DirectInteractionMode(HabitApplication(clock, InMemoryHabits()))

    return DynamicTest.dynamicTest("direct") {
        steps(directInteractionMode)
    }
}

fun fixedClock(zonedDateTime: String): Clock =
    Clock.fixed(Instant.parse(zonedDateTime), ZoneId.of("UTC"))

@TestFactory
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Scenario