/**
 * Created by igushs on 9/4/2015.
 */

package convexHullTests

import convexHull.*
import org.junit.*
import testUtils.visualize
import utils.*
import java.util.*
import kotlin.test.assertTrue
import kotlin.util.measureTimeMillis

public class TestConvexHull {
    val implementations = listOf(JarvisConvexHull, GrahamConvexHull, AndrewConvexHull, QuickHull)

    private fun testImplementations(points: List<Point>) {
        if (implementations.map { i ->
            val (s, time) = timed { i.convexHull(points) }
            println("$i: $time ms, ${s.size()} points")
            return@map s.toSet()
        }.distinct().size() != 1) {
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

    private fun circle(r: Double, n: Int): List<Point> =
            (0.0..Math.PI * 2 * (1 - 1 / n) step Math.PI * 2 / n)
                    .map { Point(r * Math.cos(it), r * Math.sin(it)) }

    @Test fun testCircle() {
        val points = circle(1.0, 10000)
        testImplementations(points)
    }

}