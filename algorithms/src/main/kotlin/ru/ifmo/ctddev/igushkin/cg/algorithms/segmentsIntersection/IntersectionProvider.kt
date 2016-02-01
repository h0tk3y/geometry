package ru.ifmo.ctddev.igushkin.cg.algorithms.segmentsIntersection

import ru.ifmo.ctddev.igushkin.cg.geometry.Point
import ru.ifmo.ctddev.igushkin.cg.geometry.Segment

/**
 * API for segments intersection.
 *
 * Created by igushs on 9/6/2015.
 */

public interface IntersectionProvider {
    fun intersection(segments: List<Segment>): List<Point>

    public companion object {
        public val DEFAULT: IntersectionProvider = BentleyOttmannIntersection
    }
}