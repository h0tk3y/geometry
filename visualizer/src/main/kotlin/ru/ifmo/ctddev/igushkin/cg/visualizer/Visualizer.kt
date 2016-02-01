package ru.ifmo.ctddev.igushkin.cg.visualizer

import ru.ifmo.ctddev.igushkin.cg.geometry.Area
import ru.ifmo.ctddev.igushkin.cg.geometry.Point
import ru.ifmo.ctddev.igushkin.cg.geometry.withEach
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import javax.swing.JPanel
import javax.swing.SwingUtilities

/**
 * Visualizes arbitrary set of [Drawable]s with zooming and basic controls.
 *
 * Created by igushs on 8/30/2015.
 */

@FunctionalInterface
public interface Coordinates {
    fun getDrawingCoordinates(p: Point): Pair<Int, Int>

    companion object {
        fun by(f: (Point) -> Pair<Int, Int>): Coordinates = object : Coordinates {
            override fun getDrawingCoordinates(p: Point): Pair<Int, Int> = f(p)
        }
    }
}

public class Visualizer : JPanel() {
    val backgroundColor = Color(30, 30, 30)

    public val drawables: MutableList<Drawable> = arrayListOf()
    private val tempDrawables: MutableList<Drawable> = arrayListOf()

    public var area: Area = Area(Point(0.0, 0.0), Point(1.0, 1.0))
        set(n: Area) {
            field = n
            currentCoordinates = coordinatesOfArea(n)
            repaint()
        }

    private var currentCoordinates: Coordinates = coordinatesOfArea(area)

    private fun coordinatesOfArea(r: Area): Coordinates = Coordinates.by {
        Pair(Math.round((it.x - r.lowerLeft.x) * width / r.width).toInt(),
             Math.round((1 - (it.y - r.lowerLeft.y) / r.height) * height).toInt())
    }

    private fun graphics() = graphics?.let {
        it.setClip(0, 0, width, height);
        (it as Graphics2D).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                            RenderingHints.VALUE_ANTIALIAS_ON)
        return@let it
    }

    //region listeners

    private var onClickListener: (Visualizer.(x: Double, y: Double) -> Unit)? = null
    public fun onClick(action: Visualizer.(x: Double, y: Double) -> Unit) {
        onClickListener = action
    }

    private var onDragListener: (Visualizer.(x0: Double, y0: Double, x1: Double, y1: Double) -> Unit)? = null
    public fun onDrag(action: Visualizer.(x0: Double, y0: Double, x1: Double, y1: Double) -> Unit) {
        onDragListener = action
    }

    private var onRightClickListener: (Visualizer.(x: Double, y: Double) -> Unit)? = null
    public fun onRightClick(action: Visualizer.(x: Double, y: Double) -> Unit) {
        onRightClickListener = action
    }

    init {
        var rmbPressPoint: Point? = null
        var originalArea: Area? = null

        var lmbPressPoint: Point? = null

        this.addMouseListener(object : MouseListener {

            override fun mousePressed(e: MouseEvent) {
                val p = pointByScreenPoint(e.x, e.y)
                when (e.button) {
                    MouseEvent.BUTTON1 -> {
                        if (onDragListener != null)
                            lmbPressPoint = p
                    }
                    MouseEvent.BUTTON3 -> {
                        rmbPressPoint = p
                        originalArea = area
                    }
                }
            }

            override fun mouseClicked(e: MouseEvent) {
                when (e.button) {
                    MouseEvent.BUTTON1 -> onClickListener
                    MouseEvent.BUTTON3 -> onRightClickListener
                    else -> null
                }?.let { it(xByScreenX(e.x), yByScreenY(e.y)) }
            }

            override fun mouseReleased(e: MouseEvent) {
                if (tempDrawables.isNotEmpty()) {
                    tempDrawables.clear()
                    repaint()
                }
                val p = pointByScreenPoint(e.x, e.y)
                when (e.button) {
                    MouseEvent.BUTTON1 -> if (lmbPressPoint != null) {
                        if (lmbPressPoint!!.x != p.x || lmbPressPoint!!.y != p.y) when {
                            onDragListener != null ->
                                onDragListener!!(lmbPressPoint!!.x, lmbPressPoint!!.y, p.x, p.y)
                            onClickListener != null ->
                                onClickListener!!(p.x, p.y)
                        }
                        lmbPressPoint = null
                    }
                    MouseEvent.BUTTON3 -> if (rmbPressPoint != null) {
                        rmbPressPoint = null
                    }
                }
            }

            override fun mouseEntered(e: MouseEvent) = Unit

            override fun mouseExited(e: MouseEvent) = Unit
        })

        addMouseMotionListener(object : MouseMotionListener {
            override fun mouseDragged(e: MouseEvent) {
                val p = pointByScreenPoint(e.x, e.y)

                if (lmbPressPoint != null) {
                    tempDrawables.clear()
                    tempDrawables.add(SegmentDrawable(Point(lmbPressPoint!!.x, lmbPressPoint!!.y),
                                                      Point(p.x, p.y), Color.GRAY))
                    repaint()
                }
                if (rmbPressPoint != null) {
                    val deltaX = p.x - rmbPressPoint!!.x - (area.lowerLeft.x - originalArea!!.lowerLeft.x)
                    val deltaY = p.y - rmbPressPoint!!.y - (area.lowerLeft.y - originalArea!!.lowerLeft.y)
                    area = Area(Point(originalArea!!.lowerLeft.x - deltaX, originalArea!!.lowerLeft.y - deltaY),
                                Point(originalArea!!.upperRight.x - deltaX, originalArea!!.upperRight.y - deltaY))
                }
            }

            override fun mouseMoved(e: MouseEvent) {
            }
        })

        this.addMouseWheelListener {
            area = area.scaleCentered(1 + it.preciseWheelRotation * 0.03, pointByScreenPoint(it.x, it.y))
        }
    }

    //endregion listeners

    fun xByScreenX(x: Int) = area.lowerLeft.x + (x.toDouble() / width) * area.width
    fun yByScreenY(y: Int) = area.lowerLeft.y + (1 - y.toDouble() / height) * area.height
    fun pointByScreenPoint(x: Int, y: Int) = Point(xByScreenX(x), yByScreenY(y))

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        (g as Graphics2D).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                           RenderingHints.VALUE_ANTIALIAS_ON)
        with(g) {
            try {
                color = backgroundColor
                fillRect(0, 0, width, height)
                (drawables + tempDrawables).withEach { draw(this@with, currentCoordinates) }
            } finally {
                dispose()
            }
        }
    }

    public inline fun remove(predicate: (Drawable) -> Boolean) {
        drawables.removeAll(drawables.filter(predicate))
        repaint()
    }

    public fun clear(): Unit = remove { true }

    public fun add(d: Drawable): Unit = add(listOf(d))

    public fun add(ds: Iterable<Drawable>) {
        drawables.addAll(ds)
        SwingUtilities.invokeLater {
            val g = graphics()
            if (g != null) {
                try {
                    for (d in ds)
                        d.draw(g, currentCoordinates)
                } finally {
                    g.dispose()
                }
            }
        }
    }
}