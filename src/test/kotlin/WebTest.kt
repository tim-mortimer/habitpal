package uk.co.kiteframe.habitpal

import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import kotlin.test.Test
import kotlin.test.assertEquals
import org.http4k.format.Jackson as json

class WebTest {
    private val someUuid = "f47ac10b-58cc-4372-a567-0e02b2c3d479"
    private val someName = "journal"
    private val someHabitType = HabitType.DAILY

    private val client = application()

    @Test
    fun `starting a daily habit`() {
        submitStartHabitRequest(id = someUuid, name = someName, type = someHabitType).shouldSucceed()
    }

    @Test
    fun `starting a habit performed multiples time a day`() {
        submitStartHabitRequest(
            id = someUuid,
            name = someName,
            type = HabitType.MULTIPLE_TIMES_A_DAY,
            times = 2
        ).shouldSucceed()
    }

    @Test
    fun `cannot start a habit with an invalid UUID`() {
        submitStartHabitRequest(id = "blah").shouldFailWith(IdIsNotAUuid)
    }

    @Test
    fun `cannot start a habit with a blank name`() {
        submitStartHabitRequest(name = "").shouldFailWith(BlankName)
        submitStartHabitRequest(name = " ").shouldFailWith(BlankName)
    }

    @Test
    fun `cannot start a multiple times a day habit without multiplicity`() {
        submitStartHabitRequest(
            type = HabitType.MULTIPLE_TIMES_A_DAY,
            times = 0
        ).shouldFailWith(NoMultiplicity)

        submitStartHabitRequest(
            type = HabitType.MULTIPLE_TIMES_A_DAY,
            times = 1
        ).shouldFailWith(NoMultiplicity)

        submitStartHabitRequest(
            type = HabitType.MULTIPLE_TIMES_A_DAY,
            times = -5
        ).shouldFailWith(NoMultiplicity)

        submitStartHabitRequest(
            type = HabitType.MULTIPLE_TIMES_A_DAY,
            times = null
        ).shouldFailWith(NoMultiplicity)
    }

    private fun submitStartHabitRequest(
        id: String = someUuid,
        name: String = someName,
        type: HabitType = someHabitType,
        times: Int? = null
    ): Response {
        val requestJson = json {
            obj(
                "id" to string(id),
                "name" to string(name),
                "habitType" to string(type.toString()),
                "times" to if (times == null) nullNode() else number(times)
            )
        }

        return client(Request(Method.POST, "/").body(requestJson.toString()))
    }

    private fun Response.shouldSucceed() {
        assertEquals(Status.OK, this.status)
    }

    private fun Response.shouldFailWith(error: StartHabitError) {
        assertEquals(Status.BAD_REQUEST, this.status)
        assertEquals(error.toMessage(), this.body.toString())
    }
}