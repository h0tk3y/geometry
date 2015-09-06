package convexHull

import utils.*
import java.util.Comparator

/**
 * Created by igushs on 8/31/2015.
 */

public interface ConvexHullProvider {
    fun convexHull(points: List<Point>): List<Point>

    public companion object {
        public val DEFAULT: ConvexHullProvider = QuickHull
    }
}