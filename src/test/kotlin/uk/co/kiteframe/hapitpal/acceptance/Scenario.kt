package uk.co.kiteframe.hapitpal.acceptance

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import uk.co.kiteframe.habitpal.HabitApplication
import uk.co.kiteframe.habitpal.persistence.InMemoryHabits
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.UUID

fun habitScenario(clock: Clock = Clock.systemUTC(), steps: InteractionMode.() -> Unit): DynamicTest {
    val directInteractionMode = DirectInteractionMode(
        HabitApplication(clock, InMemoryHabits()),
        TestContext()
    )

    return DynamicTest.dynamicTest("direct") {
        steps(directInteractionMode)
    }
}

fun fixedClock(zonedDateTime: String): Clock =
    Clock.fixed(Instant.parse(zonedDateTime), ZoneId.of("UTC"))

data class TestContext(private val idMappings: MutableMap<String, String> = mutableMapOf()) {
    fun aliasHabitIdFor(scenarioHabitId: String): String {
        if (idMappings.containsKey(scenarioHabitId)) {
            return idMappings[scenarioHabitId] ?: error("ID should exist")
        } else {
            val habitId = UUID.randomUUID().toString()
            idMappings[scenarioHabitId] = habitId
            return habitId
        }
    }

    fun scenarioHabitIdFor(habitId: String): String? {
        return idMappings.map { Pair(it.value, it.key) }.toMap()[habitId]
    }
}

@TestFactory
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Scenario