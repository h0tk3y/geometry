package polygonTriangulation

import utils.Point
import utils.Segment
import utils.TURN
import utils.turn
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by igushs on 9/6/2015.
 */


object EarClippingTriangulation : TriangulationProvider() {

    private class Edge(val segment: Segment) {
        var prev: Edge by Delegates.notNull()
        var next: Edge by Delegates.notNull()
    }

    private class EdgeList(segments: List<Segment>) {
        var firstEdge: Edge
        var size: Int = 0; private set

        init {
            firstEdge = Edge(segments.first());
            size = 1;
            var prevEdge = firstEdge
            for (s in segments drop 1) {
                ++size
                val e = Edge(s)
                e.prev = prevEdge
                prevEdge.next = e
                prevEdge = e
            }
            firstEdge.prev = prevEdge
            prevEdge.next = firstEdge
        }

        fun edges() = sequence(firstEdge, { it.next.let { if (it == firstEdge) null else it } })

        fun delete(e: Edge) {
            --size

            e.next.prev = e.prev
            e.prev.next = e.next

            if (firstEdge == e) {
                firstEdge = e.next
            }
        }

        fun insertAfter(e: Edge, after: Edge) {
            ++size

            e.next = after.next
            e.prev = after

            after.next.prev = e
            after.next = e
        }

    }

    override fun polygonTriangulation(edges: List<Segment>): List<Segment> {
        val vertexList = EdgeList(edges)
        val result = ArrayList<Segment>()
        var e = vertexList.firstEdge

        fun isConvex(s: Edge) =
                turn(s.prev.segment.from, s.segment.from, s.segment.to) != TURN.RIGHT

        fun Point.inTriangle(a: Point, b: Point, c: Point) =
                turn(a, b, this).let { it == turn(b, c, this) && it == turn(c, a, this) }

        while (vertexList.size > 3) {
            if (isConvex(e)) {
                val v = e.segment.from
                val nextV = e.segment.to
                val prevV = e.prev.segment.from

                if (vertexList.edges().none { it.segment.from.inTriangle(v, nextV, prevV) }) {
                    vertexList.delete(e)
                    vertexList.delete(e.prev)
                    val newSegment = Segment(prevV, nextV)
                    vertexList.insertAfter(Edge(newSegment), e.prev.prev)
                    result add newSegment
                }
            }
            e = e.next
        }

        return result
    }
}