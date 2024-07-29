package uk.co.kiteframe.habitpal

import io.kotest.matchers.collections.shouldBeSingleton

inline fun <T> Collection<T>.shouldHaveOneEntryMatching(fn: (T) -> Unit): Collection<T> =
    this.shouldBeSingleton { fn(it) }