package ru.ifmo.ctddev.igushkin.cg.algorithms.polygonTriangulation

import ru.ifmo.ctddev.igushkin.cg.geometry.Point
import ru.ifmo.ctddev.igushkin.cg.geometry.Segment

/**
 * Created by igushs on 9/6/2015.
 */

interface TriangulationProvider {
    fun triangulation(points: List<Point>): List<Segment>? {
        val segments = polygonLines(points)
        return when (segments) {
            null -> null
            else -> polygonTriangulation(segments)
        }
    }

    /**
     * Segments should represent edges of a polygon in counterclockwise order.
     */
    fun polygonTriangulation(edges: List<Segment>): List<Segment>

    companion object {
        val DEFAULT: TriangulationProvider = EarClippingTriangulation
    }
}