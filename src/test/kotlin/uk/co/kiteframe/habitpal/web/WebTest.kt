package uk.co.kiteframe.habitpal.web

import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.http4k.template.HandlebarsTemplates
import uk.co.kiteframe.habitpal.persistence.InMemoryHabits

fun main() {
    application(
        habits = InMemoryHabits(),
        renderer = HandlebarsTemplates().HotReload("src/main/resources")
    )
        .asServer(Undertow(8081))
        .start()
}