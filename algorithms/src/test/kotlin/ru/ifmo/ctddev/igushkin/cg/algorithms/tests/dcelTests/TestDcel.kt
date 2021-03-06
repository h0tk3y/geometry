package ru.ifmo.ctddev.igushkin.cg.algorithms.tests.dcelTests

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.ifmo.ctddev.igushkin.cg.algorithms.dcel.Dcel
import ru.ifmo.ctddev.igushkin.cg.algorithms.polygonTriangulation.polygonLines
import ru.ifmo.ctddev.igushkin.cg.algorithms.tests.circle

/**
 * Tests for [Dcel].
 *
 * Created by igushs on 9/12/2015.
 */

class TestDcel {

    @Test
    fun testEdges() {
        val edges = 100
        val c = circle(1.0, edges)
        val d = Dcel.fromPolygon(polygonLines(c)!!)

        val faces = listOf(*d.innerFaces.toTypedArray(), d.outerFace)
        faces.forEach {
            assertEquals(edges, it.traverse().count())

            it.traverseVertices.forEach {
                assertTrue(it.isSimple())
            }
        }
    }

    @Test
    fun testClipping() {
        val edges = 10
        val c = circle(1.0, edges)
        val d = Dcel.fromPolygon(polygonLines(c)!!)

        assertEquals(edges, d.innerFaces.first().traverse().count())

        val f0 = d.innerFaces.first()
        val e0 = f0.edge.traverse().first()
        d.clipEdgeStart(e0)

        assertEquals(2, d.innerFaces.size)
        assertEquals(edges - 1, f0.traverse().count())

        assertEquals(3, d.innerFaces.first { it != f0 }.traverse().count())
    }

}