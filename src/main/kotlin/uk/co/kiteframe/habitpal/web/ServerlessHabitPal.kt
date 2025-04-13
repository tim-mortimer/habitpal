package uk.co.kiteframe.habitpal.web

import org.http4k.serverless.ApiGatewayV2LambdaFunction
import org.http4k.template.HandlebarsTemplates
import uk.co.kiteframe.habitpal.HabitApplication
import uk.co.kiteframe.habitpal.persistence.InMemoryHabits
import java.time.Clock

@Suppress("unused")
class ServerlessHabitPal : ApiGatewayV2LambdaFunction(
    webApplication(
        HabitApplication(Clock.systemUTC(), InMemoryHabits()),
        HandlebarsTemplates().CachingClasspath()
    )
)