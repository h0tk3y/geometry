package ru.ifmo.ctddev.igushkin.cg.algorithms.minkowskiAddition

import ru.ifmo.ctddev.igushkin.cg.algorithms.convexHull.ConvexHullProvider
import ru.ifmo.ctddev.igushkin.cg.geometry.Point

/**
 * Provides Minkowski addition of convex hulls of the points lists
 *
 * Created by igushs on 2/9/16.
 */
object NaiveConvexMinkowskiAddition : ConvexMinkowskiAdditionProvider {
    override fun addition(shape1: List<Point>, shape2: List<Point>): List<Point> {
        val ch1 = ConvexHullProvider.DEFAULT.convexHull(shape1)
        val ch2 = ConvexHullProvider.DEFAULT.convexHull(shape2)
        val prod = ch1.flatMap { p -> ch2.map { Point(p.x + it.x, p.y + it.y) } }
        return ConvexHullProvider.DEFAULT.convexHull(prod)
    }
}