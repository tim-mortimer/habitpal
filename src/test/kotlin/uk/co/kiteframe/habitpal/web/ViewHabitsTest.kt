package uk.co.kiteframe.habitpal.web

import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.testing.Approver
import org.http4k.testing.HtmlApprovalTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import uk.co.kiteframe.habitpal.*
import uk.co.kiteframe.habitpal.persistence.InMemoryHabits
import java.time.LocalDate
import java.util.*
import kotlin.test.Test

@ExtendWith(HtmlApprovalTest::class)
class ViewHabitsTest {
    private val habits = InMemoryHabits()
    private val client = application(habits)

    @BeforeEach
    fun setup() {
        habits.save(Habit(HabitId(UUID.randomUUID()), NonBlankString("wash up")!!, Daily, LocalDate.now()))
        habits.save(
            Habit(
                HabitId(UUID.randomUUID()),
                NonBlankString("dry up")!!,
                MultipleTimesADay(Multiple(2)!!),
                LocalDate.now()
            )
        )
    }

    @Test
    fun viewing_habits(approver: Approver) {
        approver.assertApproved(client(Request(Method.GET, "/habits")))
    }
}