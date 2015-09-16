package polygonTriangulation

import utils.*

/**
 * Created by igushs on 9/6/2015.
 */

public interface TriangulationProvider {
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
    public fun polygonTriangulation(edges: List<Segment>): List<Segment>

    public companion object {
        public val DEFAULT: TriangulationProvider = EarClippingTriangulation
    }
}