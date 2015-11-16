package visualizer

import utils.Point
import utils.Segment
import java.awt.Color
import java.awt.Graphics

/**
 * Created by igushs on 9/5/2015.
 */

public interface Drawable {
    fun draw(graphics: Graphics, coords: Coordinates)
}

public open class PointDrawable(override val x: Double, override val y: Double,
                                val color: Color = Color.RED)
: Point(x, y), Drawable {

    constructor(p: Point) : this(p.x, p.y)

    open val radius = 3

    override fun draw(graphics: Graphics, coords: Coordinates) {
        val (x, y) = coords.getDrawingCoordinates(this)

        graphics.color = color
        graphics.fillOval(x - radius, y - radius, radius * 2 + 1, radius * 2 + 1)
    }
}

public class SegmentDrawable(from: Point, to: Point,
                             val color: Color = Color.WHITE)
: Segment(from, to), Drawable {

    constructor(s: Segment) : this(s.from, s.to)
    constructor(x0: Double, y0: Double, x1: Double, y1: Double): this(Point(x0, y0), Point(x1, y1))

    override fun draw(graphics: Graphics, coords: Coordinates) {
        val (x0i, y0i) = coords.getDrawingCoordinates(from)
        val (x1i, y1i) = coords.getDrawingCoordinates(to)

        graphics.color = color
        graphics.drawLine(x0i, y0i, x1i, y1i)
    }
}