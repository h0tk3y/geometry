Geometry
===

Playing around with Kotlin and computational geometry algorithms.

Algorithms implemented at the moment:
---
* Convex hull
  * Jarvis algorithm
  * Graham algorithm
  * Andrew algorithm
  * Quickhull

* Polygon triangulation
  * Ear clipping algorithm

Visualizer
---
Zoom, moving, left click, right-click and drag callbacks are implemented.

How to include one into your app:

    parent as JPanel

    val v = Visualizer()
    parent.add(v)

That's it!

Or even simpler, there's `Demo` app stub which you can use:

    val demo = Demo()
    val v = demo.visualizer()
    demo.start()

How to use:

    v as Visualizer

    v add PointDrawable(0.5, 0.5)
    v add PointDrawable(0.1, 0.3, Color.GREEN)
    v add SegmentDrawable(0.1, 0.3, 0.5, 0.5)

    v.area = Area(0.1, 0.1, 0.9, 0.9)

    v add (0..100).map { SegmentDrawable(0.0, it * 0.01, 1.0, it * 0.01 }
    v remove { it is PointDrawable }

    v onClick { x, y ->
        println("Clicked at $x, $y.")
        v add PointDrawable(x, y, someRandomColor())
    }

    v onDrag { x0, y0, x1, y1 ->
        val p0 = Point(x0, y0)
        val p1 = Point(x1, y1)
        if (distance(p0, p1) >= 0.3) {
            println("Dragged so long! From $x0, $y0 to $x1, $y1.")
            v add SegmentDrawable(p0, p1)
        }
    }