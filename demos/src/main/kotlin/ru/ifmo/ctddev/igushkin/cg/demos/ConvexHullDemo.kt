package ru.ifmo.ctddev.igushkin.cg.demos

import ru.ifmo.ctddev.igushkin.cg.algorithms.convexHull.ConvexHullProvider
import ru.ifmo.ctddev.igushkin.cg.geometry.Point
import ru.ifmo.ctddev.igushkin.cg.geometry.Segment
import ru.ifmo.ctddev.igushkin.cg.geometry.distance
import ru.ifmo.ctddev.igushkin.cg.visualizer.Demo
import ru.ifmo.ctddev.igushkin.cg.visualizer.PointDrawable
import ru.ifmo.ctddev.igushkin.cg.visualizer.SegmentDrawable
import java.util.*
import javax.swing.SwingUtilities
import kotlin.concurrent.timer


/**
 * Demonstrates convex hull on a set of points with pseudo Brownian motion.
 *
 * Created by igushs on 8/31/2015.
 */

object ConvexHullDemo {
    public val demo: Demo = Demo()
    val v = demo.visualizer

    @JvmStatic
    public fun main(args: Array<String>) {
        demo.start()

        val convexHaulProvider = ConvexHullProvider.DEFAULT

        fun apply() {
            v.remove { it is Segment }
            val hull = convexHaulProvider.convexHull(v.drawables.filterIsInstance<Point>())
            if (hull.size >= 2) {
                var prevPoint: Point = hull.first()
                val result = arrayListOf<SegmentDrawable>()
                for (i in hull.drop(1)) {
                    result.add(SegmentDrawable(prevPoint, i))
                    prevPoint = i
                }
                result.add(SegmentDrawable(prevPoint, hull.first()))
                v.add(result)
            }
        }

        v.onClick { x, y ->
            v.add(PointDrawable(x, y))
            apply()
        }
        v.onRightClick { x, y ->
            val p = Point(x, y)
            v.remove { it is Point && distance(p, it) < 0.1 }
            apply()
        }

        val rng = Random()

        timer(period = 1000 / 60) {
            SwingUtilities.invokeLater {
                val points = v.drawables.filterIsInstance<PointDrawable>()
                v.drawables.clear()
                v.drawables.addAll(points.map {
                    PointDrawable(it.x + (rng.nextDouble() - 0.5) * 0.006,
                            it.y + (rng.nextDouble() - 0.5) * 0.006)
                })
                apply()
            }
        }
    }
}