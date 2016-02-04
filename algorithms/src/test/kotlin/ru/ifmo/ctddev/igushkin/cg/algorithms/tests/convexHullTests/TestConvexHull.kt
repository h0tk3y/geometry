package ru.ifmo.ctddev.igushkin.cg.algorithms.tests.convexHullTests

import org.junit.Assert
import org.junit.Test
import ru.ifmo.ctddev.igushkin.cg.algorithms.convexHull.AndrewConvexHull
import ru.ifmo.ctddev.igushkin.cg.algorithms.convexHull.GrahamConvexHull
import ru.ifmo.ctddev.igushkin.cg.algorithms.convexHull.JarvisConvexHull
import ru.ifmo.ctddev.igushkin.cg.algorithms.convexHull.QuickHull
import ru.ifmo.ctddev.igushkin.cg.algorithms.tests.circle
import ru.ifmo.ctddev.igushkin.cg.algorithms.tests.visualize
import ru.ifmo.ctddev.igushkin.cg.geometry.Point
import ru.ifmo.ctddev.igushkin.cg.geometry.timed
import java.util.*

class TestConvexHull {
    val implementations = listOf(JarvisConvexHull, GrahamConvexHull, AndrewConvexHull, QuickHull)

    private fun testImplementations(points: List<Point>) {
        if (implementations.map { i ->
            val (s, time) = timed { i.convexHull(points) }
            println("$i: $time ms, ${s.size} points")
            return@map s.toSet()
        }.distinct().size != 1) {
            visualize(points)
            Assert.fail()
        }
    }

    @Test fun testSimple() {
        testImplementations(listOf(Point(0.0, 0.0), Point(1.1, 1.1), Point(0.0, 1.1)))
        testImplementations(listOf(Point(0.0, 0.0), Point(1.1, 1.1), Point(0.0, 1.1), Point(2.0, 0.0)))
    }

    @Test fun testRandomized() {
        val random = Random(0)
        val points = (0..2000000).map { Point(random.nextDouble(), random.nextDouble()) }
        testImplementations(points)
    }

    @Test fun testCircle() {
        val points = circle(1.0, 10000)
        testImplementations(points)
    }

}