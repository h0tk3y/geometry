package testUtils;

import utils.Point
import utils.Segment
import visualizer.Demo
import visualizer.Drawable
import visualizer.PointDrawable
import visualizer.SegmentDrawable
import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import java.util.concurrent.CountDownLatch
import javax.swing.WindowConstants

/**
 * Utils used in tests.
 *
 * Created by igushs on 9/9/2015.
 */

fun visualize(items: List<*>) {
    val d = Demo()
    d.window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
    val v = d.visualizer
    d.start()

    for (i in items)
        println(i)

    val vItems = items.map {
        when (it) {
            is Drawable -> it
            is Point -> PointDrawable(it)
            is Segment -> SegmentDrawable(it)
            else -> null
        }
    }.filterIsInstance<Drawable>()

    v.add(vItems)

    val latchFinished = CountDownLatch(1)

    v.onRightClick { x, y ->
        d.window.dispose()
        latchFinished.countDown()
    }

    d.window.addWindowListener(object : WindowListener {
        override fun windowClosed(e: WindowEvent?) {
            latchFinished.countDown()
        }

        //region unused callbacks
        override fun windowDeiconified(e: WindowEvent) {
        }

        override fun windowActivated(e: WindowEvent) {
        }

        override fun windowDeactivated(e: WindowEvent) {
        }

        override fun windowIconified(e: WindowEvent) {
        }

        override fun windowClosing(e: WindowEvent) {
        }

        override fun windowOpened(e: WindowEvent?) {
        }
        //endregion
    })

    latchFinished.await()
}

fun circle(r: Double, n: Int): List<Point> =
        (0..n - 1).map { 2 * Math.PI / n * it }.map { Point(r * Math.cos(it), r * Math.sin(it)) }