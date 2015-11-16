package convexHullTests

import convexHull.AndrewConvexHull
import convexHull.GrahamConvexHull
import convexHull.JarvisConvexHull
import convexHull.QuickHull
import org.junit.Assert
import org.junit.Test
import testUtils.circle
import testUtils.visualize
import utils.Point
import utils.timed
import java.util.*

public class TestConvexHull {
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