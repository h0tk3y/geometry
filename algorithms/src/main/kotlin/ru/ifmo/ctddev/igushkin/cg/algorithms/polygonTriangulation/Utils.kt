package ru.ifmo.ctddev.igushkin.cg.algorithms.polygonTriangulation

import ru.ifmo.ctddev.igushkin.cg.algorithms.segmentsIntersection.IntersectionProvider
import ru.ifmo.ctddev.igushkin.cg.geometry.Point
import ru.ifmo.ctddev.igushkin.cg.geometry.Segment
import ru.ifmo.ctddev.igushkin.cg.geometry.TURN
import ru.ifmo.ctddev.igushkin.cg.geometry.turn
import java.util.*

public fun polygonLines(p: List<Point>): List<Segment>? {
    if (p.distinct().size != p.size || p.size < 3)
        return null

    val leftmost = p.minBy { it.x }!!
    val lIndex = p.indexOf(leftmost)
    val lNext = p[(lIndex + 1) % p.size]
    val lPrev = p[(lIndex - 1 + p.size) % p.size]

    /** [p] in counterclockwise direction */
    val ps = if (turn(leftmost, lPrev, lNext) == TURN.RIGHT)
        p else
        p.reversed()

    val pSet = HashSet(ps)
    val segments = ps.zip(ps.drop(1) + ps.first()).map { Segment(it.first, it.second) }
    val list = IntersectionProvider.DEFAULT.intersection(segments).filterNot { it in pSet }
    return when {
        list.isNotEmpty() -> null
        else -> segments
    }
}