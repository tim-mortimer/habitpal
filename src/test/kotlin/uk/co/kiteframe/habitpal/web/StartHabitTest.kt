package uk.co.kiteframe.habitpal.web

import org.http4k.core.*
import org.http4k.core.body.form
import org.http4k.core.body.toBody
import uk.co.kiteframe.habitpal.HabitType
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class StartHabitTest {
    private val someName = "journal"

    private val client = application()

    @Test
    fun `starting a daily habit`() {
        client(startHabitRequest().withValidDailyForm()).shouldSucceed()
    }

    @Test
    fun `starting a habit performed multiples time a day`() {
        client(startHabitRequest().withValidMultipleForm()).shouldSucceed()
    }

    @Test
    fun validations() {
        val dailyRequest = startHabitRequest().withValidDailyForm()
        val multipleRequest = startHabitRequest().withValidMultipleForm()

        client(dailyRequest.formWithout("name")).shouldFailWith("formData 'name' is required")
        client(dailyRequest.replacingForm("name", "")).shouldFailWith("formData 'name' is required")
        client(dailyRequest.replacingForm("name", " ")).shouldFailWith("formData 'name' must be string")

        client(dailyRequest.formWithout("type")).shouldFailWith("formData 'type' is required")
        client(dailyRequest.replacingForm("type", "INVALID")).shouldFailWith("formData 'type' must be string")

        client(dailyRequest.formWithout("times")).shouldSucceed()
        client(dailyRequest.form("times", "")).shouldSucceed()
        client(multipleRequest.formWithout("times")).shouldFailWith("formData 'times' must be >= 2")
        client(multipleRequest.replacingForm("times", "")).shouldFailWith("formData 'times' must be >= 2")
        client(multipleRequest.replacingForm("times", "0")).shouldFailWith("formData 'times' must be >= 2")
        client(multipleRequest.replacingForm("times", "1")).shouldFailWith("formData 'times' must be >= 2")
        client(multipleRequest.replacingForm("times", "-5")).shouldFailWith("formData 'times' must be >= 2")
    }

    private fun startHabitRequest() = Request(Method.POST, "/habits")
        .header("Content-Type", ContentType.APPLICATION_FORM_URLENCODED.value)
        .header("HX-Request", "true")

    private fun Request.withValidDailyForm() = formData(
        "name" to someName,
        "type" to HabitType.DAILY.toString()
    )

    private fun Request.withValidMultipleForm() = formData(
        "name" to someName,
        "type" to HabitType.MULTIPLE_TIMES_A_DAY.toString(),
        "times" to "2"
    )

    private fun Request.formData(vararg formData: Pair<String, String>): Request =
        this.body(form().plus(formData).toBody())

    private fun Request.formWithout(name: String): Request =
        body(form().filter { it.first != name }.toBody())

    private fun Request.replacingForm(name: String, value: String) =
        formWithout(name).form(name, value)

    private fun Response.shouldSucceed() {
        assertEquals(Status.SEE_OTHER, this.status)
        assertEquals("/habits", this.header("Location"))
    }

    private fun Response.shouldFailWith(error: String) {
        assertEquals(Status.BAD_REQUEST, this.status)
        assertContains(this.body.toString(), error)
    }
}
