package demos

import segmentsIntersection.IntersectionProvider
import utils.*
import visualizer.*
import kotlin.platform.platformStatic
import kotlin.util.measureTimeMillis

/**
 * Created by igushs on 8/30/2015.
 */

object IntersectionDemo {
    public val demo: Demo = Demo()
    val v = demo.visualizer

    public platformStatic fun main(args: Array<String>) {
        demo.start()

        v onDrag { x0, y0, x1, y1 ->
            val segment = SegmentDrawable(Point(x0, y0), Point(x1, y1))
            v add segment
            val intersection = IntersectionProvider.DEFAULT.intersection(v.drawables.filterIsInstance<Segment>())
            v remove { it is Point }
            v add intersection.map { PointDrawable(it) }
        }

        v onRightClick { x, y ->
            v.clear()
        }
    }
}