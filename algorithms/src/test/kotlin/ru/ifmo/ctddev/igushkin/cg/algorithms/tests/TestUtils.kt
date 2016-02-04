package ru.ifmo.ctddev.igushkin.cg.algorithms.tests;

import ru.ifmo.ctddev.igushkin.cg.geometry.Point
import ru.ifmo.ctddev.igushkin.cg.geometry.Segment
import ru.ifmo.ctddev.igushkin.cg.visualizer.Demo
import ru.ifmo.ctddev.igushkin.cg.visualizer.Drawable
import ru.ifmo.ctddev.igushkin.cg.visualizer.PointDrawable
import ru.ifmo.ctddev.igushkin.cg.visualizer.SegmentDrawable
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
    d.window.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
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

        override fun windowDeiconified(e: WindowEvent) = Unit
        override fun windowActivated(e: WindowEvent) = Unit
        override fun windowDeactivated(e: WindowEvent) = Unit
        override fun windowIconified(e: WindowEvent) = Unit
        override fun windowClosing(e: WindowEvent) = Unit
        override fun windowOpened(e: WindowEvent?) = Unit
    })

    latchFinished.await()
}

fun circle(r: Double, n: Int): List<Point> =
        (0..n - 1).map { 2 * Math.PI / n * it }.map { Point(r * Math.cos(it), r * Math.sin(it)) }