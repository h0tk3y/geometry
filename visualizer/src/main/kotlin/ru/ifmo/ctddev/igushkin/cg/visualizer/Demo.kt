package ru.ifmo.ctddev.igushkin.cg.visualizer

import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.UIManager
import javax.swing.WindowConstants

/**
 * Basic wrapper class that contains a [Visualizer].
 *
 * Created by igushs on 8/30/2015.
 */

class Demo() {

    val visualizer = Visualizer()
    val window: JFrame

    init {
        window = JFrame("Demo")
        with(window) {
            this.minimumSize = Dimension(500, 500)
            contentPane = visualizer
            defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            setLocationRelativeTo(null)
        }
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    }

    fun start() {
        window.setVisible(true)
    }
}