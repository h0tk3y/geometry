package ru.ifmo.ctddev.igushkin.cg.algorithms.tests.segmentsIntersectionTests

import org.junit.Assert
import org.junit.Test
import ru.ifmo.ctddev.igushkin.cg.algorithms.segmentsIntersection.BentleyOttmannIntersection
import ru.ifmo.ctddev.igushkin.cg.algorithms.segmentsIntersection.NaiveIntersection
import ru.ifmo.ctddev.igushkin.cg.algorithms.tests.visualize
import ru.ifmo.ctddev.igushkin.cg.geometry.Segment
import ru.ifmo.ctddev.igushkin.cg.geometry.timed
import java.util.*

val implementations = listOf(NaiveIntersection, BentleyOttmannIntersection)

private fun testImplementations(segments: List<Segment>) {
    if (implementations.map { i ->
    val (s, time) = timed { i.intersection(segments) }
    println("$i: $time ms, ${s.size} points")
    return@map s.toSet().size
}.distinct().size != 1) {
        visualize(segments)
        Assert.fail()
    }
}

class TestIntersection {

    @Test
    fun test3Segments() {
        val iterations = 100000
        val random = Random()

        repeat(iterations) {
            val s1 = Segment(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble())
            val s2 = Segment(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble())
            val s3 = Segment(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble())
            testImplementations(listOf(s1, s2, s3))
        }
    }

    @Test
    fun testHorizontal() {
        val iterations = 100000
        val random = Random()

        repeat(iterations) {
            val hrY = random.nextDouble()
            val hr = Segment(random.nextDouble(), hrY, random.nextDouble(), hrY)
            val s1 = Segment(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble())
            val s2 = Segment(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble())
            val s3 = Segment(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble())
            testImplementations(listOf(hr, s1, s2, s3))
        }
    }

    @Test
    fun testBigHorizontal() {
        val testSize = 100
        val random = Random()



        val segments = (1..testSize).map {
            Segment(random.nextDouble(), random.nextDouble(),
                    random.nextDouble(), random.nextDouble())
        }
        val hrs = (1..testSize).map {
            val y = random.nextDouble()
            Segment(random.nextDouble(), y, random.nextDouble(), y)
        }
        testImplementations(segments + hrs)
    }

    @Test
    fun testBig() {
        val testSize = 1000
        val random = Random()   

        val segments = (1..testSize).map {
            Segment(random.nextDouble(), random.nextDouble(),
                    random.nextDouble(), random.nextDouble())
        }
        testImplementations(segments)
    }

    @Test
    fun testSparse() {
        val testSize = 10000
        val random = Random()

        val segments = (1..testSize).map {
            val x = random.nextDouble()
            val y = random.nextDouble()
            Segment(x, y, x + random.nextDouble() * 0.05, y + random.nextDouble() * 0.05)
        }
        testImplementations(segments)
    }
}
