package uk.co.kiteframe.habitpal.acceptance

import io.kotest.matchers.collections.shouldBeSingleton
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import uk.co.kiteframe.habitpal.HabitApplication
import uk.co.kiteframe.habitpal.HabitModel
import uk.co.kiteframe.habitpal.HabitType
import uk.co.kiteframe.habitpal.InMemoryHabits
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.*

fun fixedClock(zonedDateTime: String): Clock =
    Clock.fixed(Instant.parse(zonedDateTime), ZoneId.of("UTC"))

fun habitScenario(clock: Clock = Clock.systemUTC(), steps: InteractionMode.() -> Unit): DynamicTest {
    val directInteractionMode = DirectInteractionMode(HabitApplication(clock, InMemoryHabits()))

    return DynamicTest.dynamicTest("direct") {
        steps(directInteractionMode)
    }
}

inline fun <T> Collection<T>.shouldHaveOneEntryMatching(fn: (T) -> Unit): Collection<T> =
    this.shouldBeSingleton { fn(it) }

interface InteractionMode : AutoCloseable {
    fun startHabit(name: String, type: HabitType = HabitType.DAILY)
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