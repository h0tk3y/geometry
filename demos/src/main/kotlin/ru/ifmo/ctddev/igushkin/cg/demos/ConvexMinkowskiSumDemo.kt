package ru.ifmo.ctddev.igushkin.cg.demos

import ru.ifmo.ctddev.igushkin.cg.algorithms.convexHull.ConvexHullProvider
import ru.ifmo.ctddev.igushkin.cg.algorithms.minkowskiAddition.ConvexMinkowskiAdditionProvider
import ru.ifmo.ctddev.igushkin.cg.geometry.Area
import ru.ifmo.ctddev.igushkin.cg.geometry.Point
import ru.ifmo.ctddev.igushkin.cg.visualizer.Demo
import ru.ifmo.ctddev.igushkin.cg.visualizer.PointDrawable
import ru.ifmo.ctddev.igushkin.cg.visualizer.SegmentDrawable
import java.awt.Color
import java.util.*

/**
 * Demonstrates convex Minkowski sum of convex shapes.
 *
 * Created by igushs on 2/9/16.
 */

object ConvexMinkowskiSumDemo {
    val demo = Demo()
    val v = demo.visualizer

    enum class State { RESULT, FIRST, SECOND }

    var currentState = State.RESULT
    val center = PointDrawable(0.0, 0.0, Color.GRAY)

    init {
        v.area = Area(-1.0, -1.0, 1.0, 1.0)
        v.add(center)

        var first = ArrayList<PointDrawable>()
        var second = ArrayList<PointDrawable>()

        val stateToList = mapOf(State.FIRST to first, State.SECOND to second)
        val stateToColor = mapOf(State.FIRST to Color.RED.darker(),
                                 State.SECOND to Color.GREEN.darker(),
                                 State.RESULT to Color.YELLOW)

        v.onClick { x, y ->
            if (currentState == State.RESULT) {
                v.remove { it != center }
                currentState = State.FIRST
            }
            val newPoint = PointDrawable(x, y, stateToColor[currentState]!!)
            stateToList[currentState]!!.add(newPoint)
            v.add(newPoint)
        }

        v.onRightClick { _0, _1 ->
            fun joinWithSegments(points: List<Point>, color: Color) =
                    points.zip(points.drop(1) + points[0]).map { SegmentDrawable(it.first, it.second, color) }

            if (currentState != State.RESULT) {
                val hull = ConvexHullProvider.DEFAULT.convexHull(stateToList[currentState]!!)
                v.add(joinWithSegments(hull, stateToColor[currentState]!!))
            }

            currentState = when (currentState) {
                State.FIRST -> State.SECOND
                State.SECOND -> State.RESULT
                State.RESULT -> State.RESULT
            }

            if (currentState == State.RESULT) {
                val addition = ConvexMinkowskiAdditionProvider.DEFAULT.addition(stateToList[State.FIRST]!!,
                                                                                stateToList[State.SECOND]!!)
                v.add(joinWithSegments(addition, stateToColor[currentState]!!))
                first.clear()
                second.clear()
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        demo.start()
    }
}