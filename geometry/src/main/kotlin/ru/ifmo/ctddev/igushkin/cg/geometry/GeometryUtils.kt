package ru.ifmo.ctddev.igushkin.cg.geometry

/**
 * Geometry primitives and util functions.
 *
 * Created by igushs on 8/30/2015.
 */

open class Point(open val x: Double, open val y: Double) {
    override fun toString() = "$x, $y"

    override fun equals(other: Any?): Boolean {
        if (other == null || other.javaClass != javaClass)
            return false
        other as Point
        return other.x == x && other.y == y
    }

    override fun hashCode(): Int {
        return x.hashCode() + 31 * y.hashCode()
    }
}

open class Segment(val from: Point, val to: Point) {
    constructor(x0: Double, y0: Double, x1: Double, y1: Double) : this(Point(x0, y0), Point(x1, y1))

    val x0: Double = from.x
    val y0: Double = from.y

    val x1: Double = to.x
    val y1: Double = to.y

    override fun toString() = "($x0, $y0, $x1, $y1)"
}

open class Area(p1: Point, p2: Point) {

    constructor(x0: Double, y0: Double, x1: Double, y1: Double): this(Point(x0, y0), Point(x1, y1))

    val lowerLeft = Point(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y))
    val upperRight = Point(Math.max(p1.x, p2.x), Math.max(p1.y, p2.y))

    val width: Double get() = upperRight.x - lowerLeft.x
    val height: Double get() = upperRight.y - lowerLeft.y

    fun center() = Point(lowerLeft.x + 0.5 * width, lowerLeft.y + 0.5 * height)

    fun scaleCentered(ratio: Double, center: Point = center()): Area {
        val centerToLeft = center.x - lowerLeft.x
        val centerToRight = upperRight.x - center.x
        val centerToBottom = center.y - lowerLeft.y
        val centerToTop = upperRight.y - center.y

        return Area(Point(center.x - centerToLeft * ratio, center.y - centerToBottom * ratio),
                    Point(center.x + centerToRight * ratio, center.y + centerToTop * ratio))
    }
}

/**
 * Absolute value of ([b] - [a]) x ([c] - [a])
 */
fun determinant(a: Point, b: Point, c: Point): Double = (c.y - a.y) * (b.x - a.x) - (c.x - a.x) * (b.y - a.y)

fun turn(a: Point, b: Point, c: Point): Int = Math.signum(determinant(a, b, c)).toInt()

object TURN {
    val LEFT = 1
    val RIGHT = -1
    val CENTER = 0
}

fun intersectionPoint(a: Segment, b: Segment): Point? {
    val dax = a.x1 - a.x0
    val day = a.y1 - a.y0
    val dbx = b.x1 - b.x0
    val dby = b.y1 - b.y0
    val d0x = b.x0 - a.x0
    val d0y = b.y0 - a.y0

    val x: Double
    val y: Double

    if (dax == 0.0) {
        x = a.x0
        y = b.y0 + dby * (x - b.x0) / dbx
    } else if (dbx == 0.0) {
        x = b.x0
        y = a.y0 + day * (x - a.x0) / dax
    } else if (day == 0.0) {
        y = a.y0
        x = b.x0 + dbx * (y - b.y0) / dby
    } else if (dby == 0.0) {
        y = b.y0
        x = a.x0 + dax * (y - a.y0) / day
    } else {
        /** x = ax0 + t * dax = bx0 + u * dbx
         *  y = ay0 + t * day = by0 + u * dby
         *  t = (bx0 - ax0 + u * dbx) / dax = (by0 - ay0 + u * dby) / day;
         *  (d0x + u * dbx) / dax = (dy0 + u * dby) / day; */
        val u = (d0y / day - d0x / dax) / (dbx / dax - dby / day)

        if (u !in 0..1)
            return null

        x = b.x0 + u * dbx
        y = b.y0 + u * dby
    }

    return if (x.between(a.x0, a.x1) && y.between(a.y0, a.y1) &&
               x.between(b.x0, b.x1) && y.between(b.y0, b.y1))
        Point(x, y) else
        null
}

fun distance(a: Point, b: Point): Double {
    val dx = b.x - a.x
    val dy = b.y - a.y
    return Math.sqrt(dx * dx + dy * dy)
}

fun distanceToLine(p: Point, l: Segment): Double {
    val a = l.y0 - l.y1
    val b = l.x1 - l.x0
    val c = l.x0 * l.y1 - l.x1 * l.y0

    return Math.abs(a * p.x + b * p.y + c) / Math.sqrt(a * a + b * b)
}