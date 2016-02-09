package ru.ifmo.ctddev.igushkin.cg.algorithms.minkowskiAddition

import ru.ifmo.ctddev.igushkin.cg.algorithms.convexHull.ConvexHullProvider
import ru.ifmo.ctddev.igushkin.cg.geometry.Point
import ru.ifmo.ctddev.igushkin.cg.geometry.TURN
import ru.ifmo.ctddev.igushkin.cg.geometry.turn
import java.util.*

fun ensureConvex(shape: List<Point>) =
        shape.zip(shape.drop(1) + shape[0]).zip(shape.drop(2) + shape.take(2)).map {
            val (v1, v2) = it.first
            val v3 = it.second
            turn(v1, v2, v3)
        }.distinct().let {
            when (it) {
                setOf(TURN.LEFT) -> shape
                setOf(TURN.RIGHT) -> shape.asReversed()
                else -> ConvexHullProvider.DEFAULT.convexHull(shape)
            }
        }

/**
 * Provides Minkowski addition of convex hulls of the points lists with O(mn)
 * time from m and n -- sizes of the lists.
 *
 * If the shapes are not convex lists, they are transformed first.
 *
 * Created by igushs on 2/9/16.
 */
object NaiveConvexMinkowskiAddition : ConvexMinkowskiAdditionProvider {
    override fun addition(shape1: List<Point>, shape2: List<Point>): List<Point> {
        val s1 = ensureConvex(shape1)
        val s2 = ensureConvex(shape2)
        val prod = s1.flatMap { p -> s2.map { Point(p.x + it.x, p.y + it.y) } }
        return ConvexHullProvider.DEFAULT.convexHull(prod)
    }
}

object EdgeConvexMinkowskiAddition : ConvexMinkowskiAdditionProvider {
    val zero = Point(0.0, 0.0)

    override fun addition(shape1: List<Point>, shape2: List<Point>): List<Point> {
        fun <T> List<T>.getMod(i: Int) = get((i + size) % size)

        fun fromLowestPoint(points: List<Point>): List<Point> {
            val lowestIndex = points.indices.indexOfFirst {
                val curr = points[it]
                return@indexOfFirst curr.y <= points.getMod(it - 1).y &&
                                    curr.y <= points.getMod(it + 1).y
            }
            return points.drop(lowestIndex) + points.take(lowestIndex)
        }

        val s1 = fromLowestPoint(ensureConvex(shape1))
        val s2 = fromLowestPoint(ensureConvex(shape2))

        fun plus(p1: Point, p2: Point) = Point(p1.x + p2.x, p1.y + p2.y)
        fun minus(p1: Point, p2: Point) = Point(p1.x - p2.x, p1.y - p2.y)

        val answer = ArrayList<Point>()
        var i = 0
        var j = 0
        while (i < s1.size || j < s2.size) {
            answer.add(plus(s1.getMod(i), s2.getMod(j)))
            val toNext1 = minus(s1.getMod(i + 1), s1.getMod(i))
            val toNext2 = minus(s2.getMod(j + 1), s2.getMod(j))
            val turn = turn(zero, toNext1, toNext2)
            if (turn != TURN.RIGHT)
                ++j
            if (turn != TURN.LEFT)
                ++i
        }
        return answer
    }

}