package ru.ifmo.ctddev.igushkin.cg.demos

import ru.ifmo.ctddev.igushkin.cg.algorithms.segmentsIntersection.IntersectionProvider
import ru.ifmo.ctddev.igushkin.cg.geometry.Point
import ru.ifmo.ctddev.igushkin.cg.geometry.Segment
import ru.ifmo.ctddev.igushkin.cg.visualizer.Demo
import ru.ifmo.ctddev.igushkin.cg.visualizer.PointDrawable
import ru.ifmo.ctddev.igushkin.cg.visualizer.SegmentDrawable

/**
 * Created by igushs on 8/30/2015.
 */

object IntersectionDemo {
    public val demo: Demo = Demo()
    val v = demo.visualizer

    @JvmStatic
    public fun main(args: Array<String>) {
        demo.start()

        v.onDrag { x0, y0, x1, y1 ->
            val segment = SegmentDrawable(Point(x0, y0), Point(x1, y1))
            v.add(segment)
            val intersection = IntersectionProvider.DEFAULT.intersection(v.drawables.filterIsInstance<Segment>())
            v.remove { it is Point }
            v.add(intersection.map { PointDrawable(it) })
        }
    }
}