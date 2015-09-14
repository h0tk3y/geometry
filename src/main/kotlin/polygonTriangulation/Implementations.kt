package polygonTriangulation

import dcel.*
import utils.*
import java.util.*
import kotlin.properties.Delegates

/**
 * Implementations for [TriangulationProvider].
 *
 * Created by igushs on 9/6/2015.
 */


object EarClippingTriangulation : TriangulationProvider() {

    override fun polygonTriangulation(edges: List<Segment>): List<Segment> {
        val d = Dcel fromPolygon edges
        val result = ArrayList<Segment>()
        val nonClippedFace = d.innerFaces.single()

        var e = nonClippedFace.edge

        fun isConvex(s: HalfEdge) = s.from isConvexFor nonClippedFace
        fun Vertex.inTriangle(a: Vertex, b: Vertex, c: Vertex) =
                turn(a.point, b.point, point).let {
                    it == turn(b.point, c.point, point) &&
                    it == turn(c.point, a.point, point)
                }

        while (e.next.next != e) {
            var nextE = e.next
            if (isConvex(e) && e.traverseVertices.none { it.inTriangle(e.from, e.to, e.prev.from) }) {
                d clipEdgeStart e
                val newNext = e.next
                assert(newNext != nextE)
                result.add(newNext.segment())
            }
            e = nextE
        }

        return result
    }
}