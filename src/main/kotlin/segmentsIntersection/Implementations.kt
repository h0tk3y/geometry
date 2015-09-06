package segmentsIntersection

import utils.Point
import utils.Segment
import utils.intersectionPoint
import java.util.*

/**
 * Created by igushs on 9/6/2015.
 */

object NaiveIntersection : IntersectionProvider {
    override fun intersection(segments: List<Segment>): List<Point> {
        val result = ArrayList<Point>()
        for ((i, p) in segments.withIndex())
            for (q in segments drop i)
                intersectionPoint(p, q)?.let { result add it }
        return result
    }
}