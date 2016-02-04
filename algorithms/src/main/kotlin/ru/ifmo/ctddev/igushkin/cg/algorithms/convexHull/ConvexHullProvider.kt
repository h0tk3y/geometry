package ru.ifmo.ctddev.igushkin.cg.algorithms.convexHull

import ru.ifmo.ctddev.igushkin.cg.geometry.Point

/**
 * Created by igushs on 8/31/2015.
 */

interface ConvexHullProvider {
    fun convexHull(points: List<Point>): List<Point>

    companion object {
        val DEFAULT: ConvexHullProvider = QuickHull
    }
}