package ru.ifmo.ctddev.igushkin.cg.algorithms.dcel

import ru.ifmo.ctddev.igushkin.cg.geometry.*
import java.util.*

/**
 * Represents doubly connected edge list data structure.
 *
 * Created by igushs on 9/11/2015.
 */

public open class Vertex(val point: Point) {

    var halfEdge: HalfEdge by later()

    fun outHalfEdges() = sequence(halfEdge, {
        if (it.twin.next == halfEdge) null else it.twin.next
    })

    fun inHalfEdges() = outHalfEdges().map { it.twin }

    fun faces() = outHalfEdges().map { it.face }

    fun neighbours() = inHalfEdges().map { it.from }

    fun isSimple() = outHalfEdges().count() == 2

    fun edgeOfFace(f: Face) = outHalfEdges().firstOrNull { it.face == f }

    fun isConvexFor(f: Face): Boolean {
        return edgeOfFace(f)?.let {
            turn(it.from.point, it.to.point, it.prev.from.point) != TURN.RIGHT
        } ?: false
    }
}

public open class HalfEdge(val from: Vertex) {
    lateinit var next: HalfEdge
    lateinit var prev: HalfEdge
    lateinit var twin: HalfEdge

    lateinit var face: Face

    val to: Vertex get() = next.from

    fun traverse(): Sequence<HalfEdge> = sequence(this) { if (it.next == this) null else it.next }

    fun segment() = Segment(from.point, to.point)

    val traverseVertices = traverse().map { it.from }
}

public open class Face {
    var edge: HalfEdge by later()
    val inEdges: List<HalfEdge> get() = ArrayList<HalfEdge>()

    fun traverse(): Sequence<HalfEdge> = edge.traverse()
    val traverseVertices: Sequence<Vertex> get() = edge.traverseVertices
}

public class Dcel {
    lateinit var outerFace: Face
    val innerFaces = ArrayList<Face>()

    fun removeSimple(v: Vertex) {
        assert(v.isSimple())
        val created = ArrayList<HalfEdge>(2)
        for (e in v.outHalfEdges()) {
            val h = HalfEdge(e.prev.from)
            h.next.bindNext(e.next)
            e.prev.prev.bindNext(h)
            h.face = e.face

            created.add(h)
        }
        bindTwins(created[0], created[1])
    }

    private fun Vertex.inTriangle(a: Vertex, b: Vertex, c: Vertex) =
            turn(a.point, b.point, point).let {
                it == turn(b.point, c.point, point) &&
                it == turn(c.point, a.point, point)
            }

    public fun splitEdge(e: HalfEdge, p: Point): Vertex {
        val newVertex = Vertex(p)
        val edgeCont = HalfEdge(newVertex)
        val twinCont = HalfEdge(newVertex)

        edgeCont.bindNext(e.next)
        twinCont.bindNext(e.twin.next)

        e.bindNext(edgeCont)
        e.twin.bindNext(twinCont)

        edgeCont.face = e.face
        twinCont.face = e.twin.face

        return newVertex
    }

    fun clipEdgeStart(e: HalfEdge): Boolean {
        if (!e.from.isConvexFor(e.face) ||
            e.prev.prev == e.next ||
            e.traverseVertices.any { it.inTriangle(e.prev.from, e.from, e.to) }
        ) return false

        splitFace(e.next, e.prev)
        return true
    }

    fun splitFace(f: Face, v1: Vertex, v2: Vertex) {
        val eIn = v1.edgeOfFace(f)
        val eOut = v2.edgeOfFace(f)

        if (eIn == null || eOut == null)
            throw IllegalArgumentException("v1 and v2 should be incident to f.")

        splitFace(eIn, eOut)
    }

    /**
     * Splits face which both [eIn] and [eOut] belong so that
     * [eIn] will belong to the same face and [eOut] -- to a new one, that is
     * add a new edge between starts of [eIn] and [eOut].
     *
     * Caller response for the consistency of the splitting.
     *
     * Vertices of [eIn].face should be split by [eIn].from -> [eOut].from into two semi-planes.
     *
     * @param eIn [HalfEdge] that will remain in the same [Face]
     * @param eOut [HalfEdge] that will be moved into new [Face] with a chain of half-edges straight to [eIn]
     *
     * @sample
     *
     * a-----e
     * |      \
     * |  f   d
     * |      /
     * b-----c
     *
     * splitFace(a -> b, c -> d)
     *
     * a-----e
     * |*   g \
     * |  *   d
     * | f  * /
     * b-----c
     *
     */
    fun splitFace(eIn: HalfEdge, eOut: HalfEdge) {
        assert(eIn.face == eOut.face)

        val newFace = Face()
        innerFaces.add(newFace)
        newFace.edge = eOut

        val newEdge = HalfEdge(eOut.from)
        val newTwin = HalfEdge(eIn.from)
        newTwin.face = newFace

        bindTwins(newEdge, newTwin)

        eOut.prev.bindNext(newEdge)
        eIn.prev.bindNext(newTwin)

        newEdge.bindNext(eIn)
        newTwin.bindNext(eOut)

        eIn.face.edge = eIn
        newEdge.face = eIn.face
        eOut.traverse().forEach { it.face = newFace }
    }

    companion object {

        private fun HalfEdge.bindNext(e: HalfEdge) {
            this.next = e
            e.prev = this
        }

        private fun bindTwins(e: HalfEdge, g: HalfEdge) {
            e.twin = g
            g.twin = e
        }

        fun fromPolygon(polygonEdges: List<Segment>): Dcel {
            val result = Dcel()
            val outerFace = Face().after { result.outerFace = it }
            val innerFace = Face().after { result.innerFaces.add(it) }

            var prevVertex = Vertex(polygonEdges.last().from)
            var prevEdge: HalfEdge? = null

            var firstEdge: HalfEdge? = null

            for (e in polygonEdges) {
                val newVertex = Vertex(e.from)

                val edgeForward = HalfEdge(prevVertex)
                val edgeBackward = HalfEdge(newVertex)

                prevVertex.halfEdge = edgeForward

                bindTwins(edgeForward, edgeBackward)

                prevEdge?.let {
                    it.bindNext(edgeForward)
                    edgeBackward.bindNext(it.twin)
                }

                edgeForward.face = innerFace
                edgeBackward.face = outerFace

                if (firstEdge == null)
                    firstEdge = edgeForward

                prevVertex = newVertex
                prevEdge = edgeForward
            }

            if (prevEdge != null && firstEdge != null) {
                prevVertex.halfEdge = prevEdge
                prevEdge.bindNext(firstEdge)
                firstEdge.twin.bindNext(prevEdge.twin)
            }

            innerFace.edge = prevEdge!!
            outerFace.edge = prevEdge.twin

            return result
        }
    }
}