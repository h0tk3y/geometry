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

public data class PointDrawable(override val x: Double, override val y: Double,
                                val color: Color = Color.RED)
: Point(x, y), Drawable {

    constructor(p: Point) : this(p.x, p.y)

    override fun draw(graphics: Graphics, coords: Coordinates) {
        val (x, y) = coords.getDrawingCoordinates(this)

        graphics.setColor(color)
        graphics.fillOval(x - 3, y - 3, 7, 7)
    }
}

public class SegmentDrawable(from: Point, to: Point,
                             val color: Color = Color.WHITE)
: Segment(from, to), Drawable {

    constructor(s: Segment) : this(s.from, s.to)

    override fun draw(graphics: Graphics, coords: Coordinates) {
        val (x0i, y0i) = coords.getDrawingCoordinates(from)
        val (x1i, y1i) = coords.getDrawingCoordinates(to)

        graphics.setColor(color)
        graphics.drawLine(x0i, y0i, x1i, y1i)
    }
}