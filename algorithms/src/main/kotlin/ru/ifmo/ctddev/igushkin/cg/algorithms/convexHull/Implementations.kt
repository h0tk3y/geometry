package ru.ifmo.ctddev.igushkin.cg.algorithms.convexHull

import ru.ifmo.ctddev.igushkin.cg.geometry.*
import java.util.*
import kotlin.comparisons.compareBy
import kotlin.comparisons.thenBy

/**
 * Implementations of point set convex hull.
 *
 * Created by igushs on 9/6/2015.
 */

/**
 * Compares points by polar angle around [pole].
 *
 * @param pole [Point] to which all the others are in the same semi-plane.
 */
fun polarComparator(pole: Point): Comparator<in Point> =
        Comparator { o1, o2 -> -1 * turn(pole, o1, o2) }

object JarvisConvexHull : ConvexHullProvider {
    override fun convexHull(points: List<Point>): List<Point> {
        if (points.size <= 2) return points

        val firstPoint = points.maxWith(compareBy<Point> { it.y }.reversed().thenBy { it.x })!!

        val result = arrayListOf<Point>()
        do {
            val currentPoint = result.lastOrNull() ?: firstPoint
            val nextPoint = points
                    .filter { it != currentPoint }
                    .maxWith(polarComparator(currentPoint))!!
            result.add(nextPoint)
        } while (nextPoint !== firstPoint)
        return result
    }
}

object GrahamConvexHull : ConvexHullProvider {
    override fun convexHull(points: List<Point>): List<Point> {
        if (points.size <= 2) return points

        val firstPoint = points.maxWith(compareBy<Point> { it.y }.reversed().thenBy { it.x })!!

        val sortedPoints = points.filter { it != firstPoint }.sortedWith(polarComparator(firstPoint))

        val result = arrayListOf(firstPoint, sortedPoints.first())
        fun last() = result[result.indices.endInclusive]
        fun prev() = result[result.indices.endInclusive - 1]

        for (p in sortedPoints.drop(1)) {
            while (turn(prev(), last(), p) != TURN.LEFT)
                result.removeAt(result.indices.endInclusive)
            result.add(p)
        }

        return result
    }
}

object AndrewConvexHull : ConvexHullProvider {
    val comparator = compareBy<Point> { it.x }.thenBy { it.y }

    override fun convexHull(points: List<Point>): List<Point> {
        if (points.size <= 2) return points

        val leftmost = points.minBy { it.x }!!
        val rightmost = points.maxBy { it.x }!!

        val (top, bottom) = points
                .filter { it != leftmost && it != rightmost }
                .partition { turn(leftmost, rightmost, it) == TURN.LEFT }

        val result = arrayListOf(leftmost, rightmost)
        fun last() = result[result.indices.endInclusive]
        fun prev() = result[result.indices.endInclusive - 1]

        fun grahamIterate(sortedPoints: List<Point>) {
            for (p in sortedPoints) {
                while (turn(prev(), last(), p) != TURN.LEFT)
                    result.removeAt(result.indices.endInclusive)
                result.add(p)
            }
        }

        grahamIterate(top.sortedWith(comparator.reversed()) + leftmost)
        grahamIterate(bottom.sortedWith(comparator) + rightmost)

        return result.subList(1, result.size - 1)
    }
}

object QuickHull : ConvexHullProvider {
    override fun convexHull(points: List<Point>): List<Point> {
        if (points.size <= 2) return points

        val leftmost = points.minBy { it.x }!!
        val rightmost = points.maxBy { it.x }!!

        val result = arrayListOf<Point>()
        fun recurse(line: Segment, points: List<Point>) {
            val above = points.filter { turn(line.from, line.to, it) == TURN.LEFT }
            if (above.size == 0)
                return
            val farthest = above.maxWith(compareBy<Point>({ distanceToLine(it, line) }).thenBy { it.x })!!
            recurse(Segment(line.from, farthest), above)
            result.add(farthest)
            recurse(Segment(farthest, line.to), above)
        }

        result.add(leftmost)
        recurse(Segment(leftmost, rightmost), points)
        result.add(rightmost)
        recurse(Segment(rightmost, leftmost), points)

        return result
    }
}