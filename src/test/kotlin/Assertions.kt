package uk.co.kiteframe.habitpal

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

fun <L, R> Either<L, R>.should(assertions: R.() -> Unit) {
    assertions.invoke(this.getOrElse { fail("Invocation failed with ${this.left()}") })
}

fun <L, R> Either<L, R>.shouldFailWith(error: L) {
    assertTrue { this.isLeft() }
    assertEquals(error, this.leftOrNull())
}