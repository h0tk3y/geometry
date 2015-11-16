package demos

import utils.Point
import visualizer.Demo
import visualizer.Drawable
import visualizer.PointDrawable
import visualizer.SegmentDrawable
import java.awt.Color

/**
 * Created by igushs on 8/30/2015.
 */

object LeftTurnDemo {

    public val demo: Demo = Demo()
    val v = demo.visualizer

    @JvmStatic
    public fun main(args: Array<String>) {
        demo.start()

        val currentPoints = arrayListOf<Point>()

        v.onClick { x, y ->
            val p = Point(x, y)
            currentPoints.getOrNull(0)?.let {
                v.add(SegmentDrawable(it, p))
            } ?: run {
                v.add(PointDrawable(p.x, p.y, Color.YELLOW))
            }
            currentPoints.add(p)
            if (currentPoints.size == 3) {
                val result = arrayListOf<Drawable>()
                val turn = utils.turn(currentPoints[0], currentPoints[1], currentPoints[2])
                result.add(PointDrawable(currentPoints[0].x, currentPoints[0].y, when (turn) {
                    1 -> Color.GREEN
                    0 -> Color.YELLOW
                    -1 -> Color.RED
                    else -> throw RuntimeException()
                }))
                v.add(result)
                currentPoints.clear()
            }
        }
    }
}