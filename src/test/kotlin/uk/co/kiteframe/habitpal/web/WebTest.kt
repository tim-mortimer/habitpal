package uk.co.kiteframe.habitpal.web

import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.http4k.template.HandlebarsTemplates
import uk.co.kiteframe.habitpal.HabitApplication
import uk.co.kiteframe.habitpal.persistence.InMemoryHabits
import java.time.Clock

fun main() {
    webApplication(
        HabitApplication(Clock.systemUTC(), InMemoryHabits()),
        HandlebarsTemplates().HotReload("src/main/resources")
    )
        .asServer(Undertow(8081))
        .start()
}