package ru.ifmo.ctddev.igushkin.cg.algorithms.polygonTriangulation

import ru.ifmo.ctddev.igushkin.cg.algorithms.dcel.Dcel
import ru.ifmo.ctddev.igushkin.cg.geometry.Segment
import java.util.*

/**
 * Implementations for [TriangulationProvider].
 *
 * Created by igushs on 9/6/2015.
 */


object EarClippingTriangulation : TriangulationProvider {

    override fun polygonTriangulation(edges: List<Segment>): List<Segment> {
        val d = Dcel.fromPolygon(edges)
        val result = ArrayList<Segment>()

        var e = d.innerFaces.single().edge

        while (e.next.next.next != e) {
            var nextE = e.next
            if (d.clipEdgeStart(e)) {
                val newNext = e.next
                result.add(newNext.segment())
            }
            e = nextE
        }

        return result
    }
}