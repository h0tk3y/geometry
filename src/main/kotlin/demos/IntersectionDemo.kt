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
            v add SegmentDrawable(0.3619566153668119, 0.8437960072449932, 0.9485274838494047, 0.38286711896563375)
            v add SegmentDrawable(0.10344374382408894, 0.26082488964889616, 0.4850016319001357, 0.9252618028164123)
            v add SegmentDrawable(0.030023812659744298, 0.9868390700121876, 0.14244583956323076, 0.19854650862968726)
            val intersection = IntersectionProvider.DEFAULT.intersection(v.drawables.filterIsInstance<Segment>())
            v add intersection.map { PointDrawable(it) }
        }
    }
}