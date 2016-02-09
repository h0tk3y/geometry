package ru.ifmo.ctddev.igushkin.cg.algorithms.minkowskiAddition

import ru.ifmo.ctddev.igushkin.cg.geometry.Point

/**
 * Provides Minkowski addition calculation for two shapes bound to zero.
 *
 * Created by igushs on 2/9/16.
 */

interface ConvexMinkowskiAdditionProvider {
    fun addition(shape1: List<Point>, shape2: List<Point>): List<Point>

    companion object {
        val DEFAULT = EdgeConvexMinkowskiAddition
    }
}