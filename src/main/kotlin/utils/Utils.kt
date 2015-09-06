package utils

import java.util.*
import kotlin.util.measureTimeMillis

/**
 * Created by igushs on 8/30/2015.
 */

inline fun <T> Iterable<T>.withEach(action: T.() -> Unit) = forEach(action)

fun <T : Any> Iterable<T>.maxBy(comparator: Comparator<in T>): T? {
    var max = firstOrNull()
    for (t in drop(1))
        if (comparator.compare(t, max) >= 0)
            max = t
    return max
}

fun Double.between(a: Double, b: Double) = this in Math.min(a, b)..Math.max(a, b)

fun <T> T.oneOf(vararg t: T): Boolean {
    return this in t
}

inline fun <T> timed(f: () -> T): Pair<T, Long> {
    val time = System.currentTimeMillis()
    val result = f()
    return Pair(result, System.currentTimeMillis() - time)
}