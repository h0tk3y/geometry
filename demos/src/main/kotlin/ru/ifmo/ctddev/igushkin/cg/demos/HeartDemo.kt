package ru.ifmo.ctddev.igushkin.cg.demos

import ru.ifmo.ctddev.igushkin.cg.geometry.Area
import ru.ifmo.ctddev.igushkin.cg.geometry.Point
import ru.ifmo.ctddev.igushkin.cg.geometry.distance
import ru.ifmo.ctddev.igushkin.cg.visualizer.Demo
import ru.ifmo.ctddev.igushkin.cg.visualizer.PointDrawable
import java.awt.Color
import java.util.*
import kotlin.concurrent.timer

object HeartDemo {
    val d = Demo()
    val v = d.visualizer

    private class Particle(override var x: Double, override var y: Double,
                           override val radius: Int = 1 + random.nextInt(4))
    : PointDrawable(x, y, Color(200 + random.nextInt(56), 0, 0)) {

        var vx: Double = 0.0
        var vy: Double = 0.0

        var ax: Double = 0.0
        var ay: Double = 0.0

        fun tick(t: Double) {
            x += vx * t
            y += vy * t

            vx += ax * t
            vy += ay * t

            if (vx > 1 || vx < -1)
                vx *= 0.95
            if (vy > 1 || vy < -1)
                vy *= 0.95
        }
    }

    infix fun Double.pow(d: Double) = when (d) {
        2.0 -> this * this
        3.0 -> this * this * this
        else -> Math.pow(this, d)
    }

    class Curve(val fx: (Double) -> Double, val fy: (Double) -> Double) {
        private fun point(t: Double = random.nextDouble()) = Particle(fx(t), fy(t))
    }

    private fun heartY(t: Double) = 13 * Math.cos(t) - 5 * Math.cos(2 * t) -
                                    2 * Math.cos(3 * t) - Math.cos(4 * t)

    private fun heartX(t: Double) = 16 * (Math.sin(t) pow 3.0)

    val heart = Curve({ heartX(it) }, { heartY(it) })


    val random = Random()

    fun Random.nextRadian(): Double = nextDouble() * 2 * Math.PI

    fun Area.randPoint() = Particle(lowerLeft.x + (upperRight.x - lowerLeft.x) * random.nextDouble(),
                                    lowerLeft.y + (upperRight.y - lowerLeft.y) * random.nextDouble())


    @JvmStatic
    fun main(args: Array<String>) {
        v.area = Area(-20.0, -23.0, 20.0, 17.0)
        val curve = heart

        data class PointRadian(val p: Particle, var t: Double, val speed: Double)

        val particles = ArrayList((0..1000).map {
            val t = random.nextRadian()
            PointRadian(v.area.randPoint(), t, random.nextDouble() + 0.5)
        })

        v.add(particles.map { it.p })
        timer(period = 1) {
            for (i in particles.indices) {
                val (p, t, s) = particles[i]
                val rx = curve.fx(t)
                val ry = curve.fy(t)
                val dist = Math.sqrt((p.x - rx) * (p.x - rx) + (p.y - ry) * (p.y - ry))
                val d = 3 * Math.max(dist, 0.01) pow 0.7
                p.ax = (rx - p.x) / d
                p.ay = (ry - p.y) / d
                p.tick(s * 0.01)
            }
            v.repaint()
        }

        timer(period = 6) {
            var sign = 1
            for (pair in particles) {
                pair.t += 0.002 * sign
                if (pair.t >= 1000 * Math.PI)
                    pair.t = 0.0
                sign *= -1
            }
        }
        d.start()

        v.onClick { x, y ->
            val z = Point(x, y)
            for (i in particles.indices) {
                val (p, _0, _1) = particles[i]
                val d = 5 / distance(p, z) pow 2.0
                p.vx += 2 * (p.x - x) * d
                p.vy += 2 * (p.y - y) * d
            }
        }

        v.onRightClick { x, y ->
            val newPs = (0..10).map {
                PointRadian(Particle(x + random.nextDouble() * 0.5, y + random.nextDouble() * 0.5, 1 + random.nextInt(3)),
                        random.nextRadian(), random.nextDouble() + 0.5)
            }
            v.add(newPs.map { it.p })
            particles.addAll(newPs)
        }
    }
}