package polygonTriangulation

import segmentsIntersection.IntersectionProvider
import utils.Point
import utils.Segment
import utils.polygonLines
import java.util.*

/**
 * Created by igushs on 9/6/2015.
 */

public abstract class TriangulationProvider {
    public fun triangulation(points: List<Point>): List<Segment>? {
        val segments = polygonLines(points)
        return when (segments) {
            null -> null
            else -> polygonTriangulation(segments)
        }
    }

    /**
     * Segments should represent edges of a polygon in counterclockwise order.
     */
    public abstract fun polygonTriangulation(edges: List<Segment>): List<Segment>

    public companion object {
        public val DEFAULT: TriangulationProvider = EarClippingTriangulation
    }
}