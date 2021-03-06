package ru.ifmo.ctddev.igushkin.cg.geometry

import java.util.*
import kotlin.properties.Delegates

/**
 * Utils used in the project code.
 *
 * Created by igushs on 8/30/2015.
 */

inline fun <T> Iterable<T>.withEach(action: T.() -> Unit) = forEach(action)

@Suppress("unused")
fun <T : Any> Iterable<T>.minBy(comparator: Comparator<in T>): T? = maxWith(comparator.reversed())

fun Double.between(a: Double, b: Double) = this in Math.min(a, b)..Math.max(a, b)

inline fun <T> timed(f: () -> T): Pair<T, Long> {
    val time = System.currentTimeMillis()
    val result = f()
    return Pair(result, System.currentTimeMillis() - time)
}

fun <T> Comparator<in T>.min(t1: T, t2: T) = when (compare(t1, t2)) {
    1 -> t2
    else -> t1
}

fun <T> Comparator<in T>.max(t1: T, t2: T) = reversed().min(t1, t2)

fun <T: Any> later() = Delegates.notNull<T>()

fun <T> T.after(action: (T) -> Unit): T {
    action(this)
    return this
}