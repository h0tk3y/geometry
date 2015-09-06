package segmentsIntersection

import utils.Point
import utils.Segment

/**
 * Created by igushs on 9/6/2015.
 */

public interface IntersectionProvider {
    fun intersection(segments: List<Segment>): List<Point>

    public companion object {
        public val DEFAULT: IntersectionProvider = NaiveIntersection
    }
}