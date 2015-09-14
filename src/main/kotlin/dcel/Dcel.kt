package dcel

import utils.*
import java.util.*
import kotlin.properties.Delegates

/**
 * Represents doubly connected edge list data structure.
 *
 * Created by igushs on 9/11/2015.
 */

public open class Vertex(val point: Point) {

    var halfEdge: HalfEdge by lateInit()

    fun outHalfEdges() = sequence(halfEdge, {
        if (it.twin.next == halfEdge) null else it.twin.next
    })

    fun inHalfEdges() = outHalfEdges().map { it.twin }

    fun neighbours() = inHalfEdges().map { it.from }

    fun isSimple() = outHalfEdges().count() == 2

    fun isConvexFor(f: Face): Boolean {
        return outHalfEdges().firstOrNull { it.face == f }?.let {
            turn(it.from.point, it.to.point, it.prev.from.point) != TURN.RIGHT
        } ?: false
    }
}

public open class HalfEdge(val from: Vertex) {
    var next: HalfEdge by lateInit()
    var prev: HalfEdge by lateInit()
    var twin: HalfEdge by lateInit()

    var face: Face by lateInit()

    val to: Vertex get() = next.from

    fun traverse(): Sequence<HalfEdge> = sequence(this) { if (it.next == this) null else it.next }

    fun segment() = Segment(from.point, to.point)

    val traverseVertices = traverse().map { it.from }
}

public open class Face {
    var edge: HalfEdge by lateInit()
    val inEdges = ArrayList<HalfEdge>()

    fun traverse(): Sequence<HalfEdge> = edge.traverse()
    val traverseVertices: Sequence<Vertex> get() = edge.traverseVertices
}

public class Dcel {
    var outerFace: Face by lateInit()
    val innerFaces = ArrayList<Face>()

    private fun HalfEdge.bindNext(e: HalfEdge) {
        this.next = e
        e.prev = this
    }

    private fun bindTwins(e: HalfEdge, g: HalfEdge) {
        e.twin = g
        g.twin = e
    }

    fun removeSimpleVertex(v: Vertex) {
        assert(v.isSimple())
        val created = ArrayList<HalfEdge>(2)
        for (e in v.outHalfEdges()) {
            val h = HalfEdge(e.prev.from)
            h.next = e.next
            e.next.prev = h
            h.prev = e.prev.prev
            e.prev.prev.next = h
            h.face = e.face

            created add h
        }
        bindTwins(created[0], created[1])
    }

    fun clipEdgeStart(e: HalfEdge) {
        assert(e.from isConvexFor e.face)
        assert(e.prev.prev != e.next)

        val newEdge = HalfEdge(e.prev.from)
        val newTwin = HalfEdge(e.to)
        bindTwins(newEdge, newTwin)
        newEdge.face = e.face

        val newFace = Face()

        if (e.face.edge == e)
            e.face.edge = e.next

        e.face = newFace
        e.prev.face = newFace
        newTwin.face = newFace
        newFace.edge = e

        e.prev.prev bindNext newEdge
        newEdge bindNext e.next

        newTwin bindNext e.prev
        e bindNext newTwin

        innerFaces.add(newFace)
    }

    companion object {
        fun fromPolygon(polygonEdges: List<Segment>): Dcel {
            val result = Dcel()
            val outerFace = Face() after { result.outerFace = it }
            val innerFace = Face() after { result.innerFaces add it }

            var prevVertex = Vertex(polygonEdges.last().from)
            var prevEdge: HalfEdge? = null

            var firstEdge: HalfEdge? = null

            for (e in polygonEdges) {
                val currentVertex = Vertex(e.from)

                val edgeForward = HalfEdge(prevVertex)
                val edgeBackward = HalfEdge(currentVertex)

                prevVertex.halfEdge = edgeForward

                edgeForward.twin = edgeBackward
                edgeBackward.twin = edgeForward

                prevEdge?.let {
                    edgeForward.prev = it
                    it.next = edgeForward
                    edgeBackward.next = it.twin
                    it.twin.prev = edgeBackward
                }

                edgeForward.face = innerFace
                edgeBackward.face = outerFace

                if (firstEdge == null)
                    firstEdge = edgeForward

                prevVertex = currentVertex
                prevEdge = edgeForward
            }

            if (prevEdge != null) {
                prevVertex.halfEdge = prevEdge
                prevEdge.next = firstEdge!!
                firstEdge.prev = prevEdge
                prevEdge.twin.prev = firstEdge.twin
                firstEdge.twin.next = prevEdge.twin
            }

            innerFace.edge = prevEdge!!
            outerFace.edge = prevEdge.twin

            return result
        }
    }
}