package segmentsIntersection

import utils.*
import java.util.*

/**
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

object BentleyOttmanIntersection : IntersectionProvider {

    val lexComparator = compareBy<Point> { it.y } thenBy { it.x }

    private class Event {
        val start: HashSet<Segment> = HashSet()
        val end: HashSet<Segment> = HashSet()
        val intersect: HashSet<Segment> = HashSet()
    }

    override fun intersection(segments: List<Segment>): List<Point> {
        if (segments.size() < 2)
            return listOf()

        val events = TreeMap<Point, Event>(lexComparator)
        for (s in segments) {
            val start = lexComparator.min(s.from, s.to)
            events.getOrPut(start) { Event() }.start add s

            val end = lexComparator.max(s.from, s.to)
            events.getOrPut(end) { Event() }.end add s
        }

        var sweepY = events.firstKey()!!.y - 1
        var prevY = sweepY
        var sweepX = events.firstKey()!!.x
        val plusInf = events.lastKey()!!.y + 1

        /** Not a good [Comparator] since its order changes. But with careful use it'll be OK. */
        val sweepLineComparator = compareBy<Segment> {
            val dy = it.to.y - it.from.y
            if (dy == 0.0) sweepX else
                it.from.x + (it.to.x - it.from.x) * (sweepY - it.from.y) / dy
        } thenBy { lexComparator.max(it.from, it.to).x }

        val status = TreeSet<Segment>(sweepLineComparator)
        val result = ArrayList<Point>()


        while (events.isNotEmpty()) {
            val (p, e) = events.firstEntry()
            events.remove(p)

            fun checkIntersection(s1: Segment?, s2: Segment?): Point? {
                if (s1 != null && s2 != null) {
                    val m1: Segment
                    val m2: Segment
                    if (lexComparator.compare(s1.from, s2.from) == 1) {
                        m1 = s1
                        m2 = s2
                    } else {
                        m1 = s2
                        m2 = s1
                    }
                    val i = intersectionPoint(m1, m2)
                    if (i != null && lexComparator.compare(i, p) > 0) {
                        sweepY = Math.min(sweepY, i.y)
                        val event = events.getOrPut(i) { Event() }
                        event.intersect add s1
                        event.intersect add s2
                    }
                    return i
                }
                return null
            }

            if (e.intersect.isNotEmpty() || e.start.size() + e.end.size() > 1)
                result add p

            if (e.end.isNotEmpty()) {
                val left = status.headSet(e.end.first()).lastOrNull { it !in e.end }
                val right = status.tailSet(e.end.first()).firstOrNull { it !in e.end }
                checkIntersection(left, right)
            }
            status removeAll e.end
            status removeAll e.intersect

            events.keySet().firstOrNull()
            sweepY = p.y
            sweepX = p.x * (1 + 1e-15)

            val nextEvent = events.keySet().firstOrNull()
            var nextY = nextEvent?.y ?: plusInf

            val toAdd = (e.intersect.filterNot { it in e.end } + e.start).sortBy(sweepLineComparator)
            for (t in toAdd) {
                status add t
                val left = status.headSet(t).lastOrNull { it !in toAdd }
                val right = status.tailSet(t).firstOrNull { it !in toAdd }
                checkIntersection(t, left)?.let { nextY = nextY coerceAtMost it.y }
                checkIntersection(t, right)?.let { nextY = nextY coerceAtMost it.y }
                status remove t
            }

            sweepY = (p.y + nextY) / 2
            status addAll toAdd
        }

        return result
    }
}