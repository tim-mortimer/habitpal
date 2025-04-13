package uk.co.kiteframe.habitpal.web;

import org.http4k.core.ContentType
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.template.HandlebarsTemplates
import org.junit.jupiter.api.Test;
import uk.co.kiteframe.habitpal.HabitApplication
import uk.co.kiteframe.habitpal.HabitId
import uk.co.kiteframe.habitpal.NonBlankString
import uk.co.kiteframe.habitpal.persistence.InMemoryHabits
import java.time.Clock
import java.util.*
import kotlin.test.assertEquals

class ArchiveHabitTest {
    private val application = HabitApplication(Clock.systemUTC(), InMemoryHabits())
    private val client = webApplication(application, HandlebarsTemplates().CachingClasspath())
    private val habitId = UUID.randomUUID()

    @Test
    fun `archiving a habit`() {
        application.startDailyHabit(HabitId(habitId), NonBlankString("Do the chores")!!)

        val response = client(anArchiveHabitRequest(habitId.toString()))

        assertEquals(Status.OK, response.status)
        assertEquals(emptyList(), application.viewHabits())
    }

    @Test
    fun validations() {
        val response = client(anArchiveHabitRequest("asdf"))
        assertEquals(Status.BAD_REQUEST, response.status)
    }

    private fun anArchiveHabitRequest(habitId: String) = Request(Method.PUT, "/habits/${habitId}/archive")
        .header("Content-Type", ContentType.APPLICATION_FORM_URLENCODED.value)
        .header("HX-Request", "true")
}
