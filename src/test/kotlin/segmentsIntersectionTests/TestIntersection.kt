package segmentsIntersectionTests

import org.junit.Assert
import org.junit.Test
import segmentsIntersection.BentleyOttmanIntersection
import segmentsIntersection.NaiveIntersection
import utils.Point
import utils.Segment
import utils.timed
import visualizer.Demo
import visualizer.PointDrawable
import visualizer.SegmentDrawable
import java.awt.Color
import java.util.*
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Created by igushs on 9/8/2015.
 */
val implementations = listOf(NaiveIntersection, BentleyOttmanIntersection)

private fun testImplementations(segments: List<Segment>) {
    if (implementations.map { i ->
        val (s, time) = timed { i.intersection(segments) }
        println("$i: $time ms, ${s.size()} points")
        return@map s.toSet().size()
    }.distinct().size() != 1) {
        val d = Demo()
        val v = d.visualizer
        d.start()

        v add segments.map { SegmentDrawable(it) }
        v add BentleyOttmanIntersection.intersection(segments).map { PointDrawable(it) }
        v add NaiveIntersection.intersection(segments).map { PointDrawable(it.x + 0.004, it.y + 0.004, Color.green) }

        for (s in segments) {
            println(s)
        }

        Thread.sleep(1000000000)
    }
}

public class TestIntersection {
    @Test
    fun testRandom2Segments() {
        val random = Random(0)
        for (i in 1..1000) {
            val s1 = Segment(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble())
            val s2 = Segment(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble())
            testImplementations(listOf(s1, s2))
        }
    }

    @Test
    fun testRandom3Segments() {
        val random = Random(4)
        for (i in 1..100000) {
            val s1 = Segment(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble())
            val s2 = Segment(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble())
            val s3 = Segment(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble())
            testImplementations(listOf(s1, s2, s3))
        }
    }
}
