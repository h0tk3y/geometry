package segmentsIntersection

import utils.*
import java.util.*

/**
 * Implementations of line intersection,
 * Created by igushs on 9/6/2015.
 */

object NaiveIntersection : IntersectionProvider {
    override fun intersection(segments: List<Segment>): List<Point> {
        val result = ArrayList<Point>()
        for ((i, p) in segments.withIndex())
            for (q in segments drop i + 1)
                intersectionPoint(p, q)?.let { result add it }
        return result
    }
}

object BentleyOttmannIntersection : IntersectionProvider {

    val lexComparator = compareBy<Point> { it.y } thenBy { it.x }

    private class Event {
        val start: HashSet<Segment> = HashSet()
        val end: HashSet<Segment> = HashSet()
        val intersect: HashSet<Segment> = HashSet()
    }

    override fun intersection(segments: List<Segment>): List<Point> {
        if (segments.size() < 2)
            return listOf()

        /**
         *  Sorted event points
         *  In this implementation the sweep line goes from bottom to top, from left to right
         *  Initially contains event points for starts and ends of the segments.
         */
        val events = TreeMap<Point, Event>(lexComparator)
        for (s in segments) {
            val start = lexComparator.min(s.from, s.to)
            events.getOrPut(start) { Event() }.start add s

            val end = lexComparator.max(s.from, s.to)
            events.getOrPut(end) { Event() }.end add s
        }

        /**
         * It's rather a sweep point then a sweep line.
         * This is for horizontal segments handling.
         */
        var sweepY = events.firstKey().y
        var sweepX = events.firstKey().x

        /**
         * Orders segments along the sweep line. Horizontal segments
         * have value of sweepX. We'll change sweepY carefully, so
         * nothing will be broken.
         */
        val sweepLineComparator = compareBy<Segment> {
            val dx = it.to.x - it.from.x
            val dy = it.to.y - it.from.y
            if (dy == 0.0) sweepX else
                it.from.x + dx * (sweepY - it.from.y) / dy
        }

        /** Segments which are crossing the sweep line, ordered from left to right. */
        val status = TreeSet<Segment>(sweepLineComparator)
        val result = ArrayList<Point>()

        while (events.isNotEmpty()) {
            val (p, e) = events.firstEntry()
            events.remove(p)

            sweepX = p.x

            /**
             * Checks whether [s1] and [s2] intersect and whether the intersection
             * point hasn't been traversed yet. If so, adds the intersection entry
             * to the corresponding event.
             */
            fun checkIntersection(s1: Segment?, s2: Segment?): Point? {
                if (s1 != null && s2 != null) {
                    val s1Less = lexComparator.compare(s1.from, s2.from) == -1
                    val m1 = if (s1Less) s1 else s2
                    val m2 = if (s1Less) s2 else s1
                    val i = intersectionPoint(m1, m2)
                    if (i != null && lexComparator.compare(i, p) > 0) {
                        val event = events.getOrPut(i) { Event() }
                        event.intersect add s1
                        event.intersect add s2
                        return i
                    }
                }
                return null
            }

            /** Point p belongs to more than one segment. */
            if (e.intersect.isNotEmpty() || e.start.size() + e.end.size() > 1)
                result add p

            if (e.end.isNotEmpty()) {
                val left = status.headSet(e.end.first()).lastOrNull { it !in e.end }
                val right = status.tailSet(e.end.first()).firstOrNull { it !in e.end }
                checkIntersection(left, right)
            }
            status removeAll e.end
            status removeAll e.intersect

            sweepY = p.y
            sweepX = p.x

            val nextEvent = events.keySet().firstOrNull()

            /** Next sweep line position. Will be used below to insert toAdd into status in the correct order. */
            var nextPoint = nextEvent ?: Point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)

            val toAdd = (e.intersect.filterNot { it in e.end } + e.start)
            for (t in toAdd) {
                status add t
                val left = status.headSet(t).lastOrNull { it !in toAdd }
                checkIntersection(t, left)?.let {
                    nextPoint = lexComparator.min(nextPoint, it)
                }
                val right = status.tailSet(t).firstOrNull { it !in toAdd }
                checkIntersection(t, right)?.let {
                    nextPoint = lexComparator.min(nextPoint, it)
                }
                status remove t
            }

            sweepY = (p.y + nextPoint.y) / 2
            sweepX = (p.x + nextPoint.x) / 2
            status addAll toAdd
        }

        return result
    }
}