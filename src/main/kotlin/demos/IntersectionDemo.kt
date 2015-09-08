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
            v add SegmentDrawable(0.6687018050632422, 0.7152131202725951, 0.1848579417288444, 0.7152131202725951)
            v add SegmentDrawable(0.647067151348319, 0.9890698279257554, 0.1511013924971234, 0.10827259772160946)
            v add SegmentDrawable(0.7700707127297707, 0.5292838996745933, 0.1177947890768255, 0.31046004171394836)
            v add SegmentDrawable(0.9890280122521472, 0.5546940099587327, 0.16241854909206366, 0.48065688954626695)
            val intersection = IntersectionProvider.DEFAULT.intersection(v.drawables.filterIsInstance<Segment>())
            v add intersection.map { PointDrawable(it) }
        }
    }
}