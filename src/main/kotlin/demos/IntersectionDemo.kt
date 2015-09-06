package demos

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

        v.onDrag { x0, y0, x1, y1 ->
            val segment = Segment(x0, y0, x1, y1)
            val result = arrayListOf<Drawable>()
            result add SegmentDrawable(segment)
            for (s in v.drawables.filterIsInstance<Segment>()) {
                val p = intersectionPoint(segment, s)
                if (p != null)
                    result add visualizer.PointDrawable(p)
            }
            v add result
        }
    }
}