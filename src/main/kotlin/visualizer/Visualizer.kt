package visualizer

import utils.Point
import utils.Rect
import utils.Segment
import utils.withEach
import java.awt.*
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import javax.swing.JPanel
import javax.swing.SwingUtilities
import kotlin.properties.Delegates
import kotlin.swing.height
import kotlin.swing.width

/**
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

    public var linesDrawingEnabled: Boolean = true

    public val drawables: MutableList<Drawable> = arrayListOf()
    public val tempDrawables: MutableList<Drawable> = arrayListOf()

    public var area: Rect = Rect(Point(0.0, 0.0), Point(1.0, 1.0))
        private set(n: Rect) {
            $area = n
            currentCoordinates = coordinatesByRect(n)
            repaint()
        }

    private var currentCoordinates: Coordinates = coordinatesByRect(area)

    private fun coordinatesByRect(r: Rect): Coordinates = Coordinates.by {
        Pair(Math.round((it.x - r.lowerLeft.x) * width / r.width).toInt(),
             Math.round((1 - (it.y - r.lowerLeft.y) / r.height) * height).toInt())
    }

    private fun graphics() = getGraphics()?.let {
        it.setClip(0, 0, getWidth(), getHeight());
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
        var rmbPressPoint: Point?
        var originalArea: Rect

        var lmbPressPoint: Point?

        this.addMouseListener(object : MouseListener {

            override fun mousePressed(e: MouseEvent) {
                val p = pointByScreenPoint(e.getX(), e.getY())
                when (e.getButton()) {
                    MouseEvent.BUTTON1 -> if (linesDrawingEnabled) lmbPressPoint = p
                    MouseEvent.BUTTON3 -> {
                        rmbPressPoint = p
                        originalArea = area
                    }
                }
            }

            override fun mouseClicked(e: MouseEvent) {
                when (e.getButton()) {
                    MouseEvent.BUTTON1 -> onClickListener
                    MouseEvent.BUTTON3 -> onRightClickListener
                    else -> null
                }?.let { it(xByScreenX(e.getX()), yByScreenY(e.getY())) }
            }

            override fun mouseReleased(e: MouseEvent) {
                if (tempDrawables.isNotEmpty()) {
                    tempDrawables.clear()
                    repaint()
                }
                if (lmbPressPoint != null) {
                    val p = pointByScreenPoint(e.getX(), e.getY())
                    onDragListener?.let {
                        if (lmbPressPoint!!.x != p.x || lmbPressPoint!!.y != p.y)
                            it(lmbPressPoint!!.x, lmbPressPoint!!.y, p.x, p.y)
                    }
                    lmbPressPoint = null
                }
                if (rmbPressPoint != null) {
                    rmbPressPoint = null
                }
            }

            override fun mouseEntered(e: MouseEvent) {
            }

            override fun mouseExited(e: MouseEvent) {
            }
        })

        addMouseMotionListener(object : MouseMotionListener {
            override fun mouseDragged(e: MouseEvent) {
                val p = pointByScreenPoint(e.getX(), e.getY())

                if (lmbPressPoint != null) {
                    tempDrawables.clear()
                    tempDrawables.add(SegmentDrawable(Point(lmbPressPoint!!.x, lmbPressPoint!!.y),
                                                      Point(p.x, p.y), Color.GRAY))
                    repaint()
                }
                if (rmbPressPoint != null) {
                    val deltaX = p.x - rmbPressPoint!!.x - (area.lowerLeft.x - originalArea.lowerLeft.x)
                    val deltaY = p.y - rmbPressPoint!!.y - (area.lowerLeft.y - originalArea.lowerLeft.y)
                    area = Rect(Point(originalArea.lowerLeft.x - deltaX, originalArea.lowerLeft.y - deltaY),
                                Point(originalArea.upperRight.x - deltaX, originalArea.upperRight.y - deltaY))
                }
            }

            override fun mouseMoved(e: MouseEvent) {
            }
        })

        this.addMouseWheelListener {
            area = area.scaleCentered(1 + it.getPreciseWheelRotation() * 0.03, pointByScreenPoint(it.getX(), it.getY()))
        }
    }

    //endregion listeners

    fun xByScreenX(x: Int) = area.lowerLeft.x + (x.toDouble() / width) * area.width
    fun yByScreenY(y: Int) = area.lowerLeft.y + (1 - y.toDouble() / height) * area.height
    fun pointByScreenPoint(x: Int, y: Int) = Point(xByScreenX(x), yByScreenY(y))

    override synchronized fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        (g as Graphics2D).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                           RenderingHints.VALUE_ANTIALIAS_ON)
        with(g) {
            try {
                setColor(backgroundColor)
                fillRect(0, 0, width, height)
                synchronized(drawables) {
                    (drawables + tempDrawables).withEach { draw(this@with, currentCoordinates) }
                }
            } finally {
                dispose()
            }
        }
    }

    public fun remove(predicate: (Drawable) -> Boolean) {
        drawables.removeAll(drawables.filter(predicate))
        repaint()
    }

    public fun clear(): Unit = remove { true }

    public fun add(d: Drawable): Unit = add(listOf(d))

    public fun add(ds: Iterable<Drawable>) {
        drawables addAll ds
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