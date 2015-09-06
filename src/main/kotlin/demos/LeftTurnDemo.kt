package demos

import utils.*
import visualizer.*
import java.awt.Color
import kotlin.platform.platformStatic
import kotlin.util.measureTimeMillis

/**
 * Created by igushs on 8/30/2015.
 */

object LeftTurnDemo {

    public val demo: Demo = Demo()
    val v = demo.visualizer

    public platformStatic fun main(args: Array<String>) {
        demo.start()

        val currentPoints = arrayListOf<Point>()

        v.onClick { x, y ->
            val p = Point(x, y)
            currentPoints add p
            if (currentPoints.size() == 3) {
                val result = arrayListOf<Drawable>()
                val turn = utils.turn(currentPoints[0], currentPoints[1], currentPoints[2])
                result add PointDrawable(currentPoints[0].x, currentPoints[0].y, if (turn > 0) Color.GREEN else Color.RED)
                result add SegmentDrawable(currentPoints[0], currentPoints[1])
                result add SegmentDrawable(currentPoints[0], currentPoints[2])
                v add result
                currentPoints.clear()
            }
        }
    }
}