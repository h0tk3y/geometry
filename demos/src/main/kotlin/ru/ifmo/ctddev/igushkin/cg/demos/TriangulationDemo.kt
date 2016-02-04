package ru.ifmo.ctddev.igushkin.cg.demos

import ru.ifmo.ctddev.igushkin.cg.algorithms.polygonTriangulation.TriangulationProvider
import ru.ifmo.ctddev.igushkin.cg.algorithms.polygonTriangulation.polygonLines
import ru.ifmo.ctddev.igushkin.cg.geometry.Point
import ru.ifmo.ctddev.igushkin.cg.geometry.Segment
import ru.ifmo.ctddev.igushkin.cg.visualizer.Demo
import ru.ifmo.ctddev.igushkin.cg.visualizer.PointDrawable
import ru.ifmo.ctddev.igushkin.cg.visualizer.SegmentDrawable
import java.util.*

/**
 * Created by igushs on 9/6/2015.
 */

object TriangulationDemo {

    val demo = Demo()
    val v = demo.visualizer

    @JvmStatic fun main(args: Array<String>) {
        demo.start()

        val currentPoints = ArrayList<Point>()

        v.onClick { x, y ->
            if (currentPoints.isEmpty())
                v.clear()
            val p = Point(x, y)
            currentPoints.lastOrNull()?.let { v.add(SegmentDrawable(it, p)) }
            currentPoints.add(p)
            v.add(PointDrawable(p))
        }

        v.onRightClick { x, y ->
            clear()
            val polygonLines = polygonLines(currentPoints)
            if (polygonLines != null) {
                remove { it is Segment }
                val triangulation = TriangulationProvider.DEFAULT.polygonTriangulation(polygonLines)
                add(currentPoints.map { PointDrawable(it) })
                polygonLines.let { v.add(it.map { SegmentDrawable(it) }) }
                triangulation.let { v.add(it.map { SegmentDrawable(it) }) }
            }
            currentPoints.clear()
        }
    }

}