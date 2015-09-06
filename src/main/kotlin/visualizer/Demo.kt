package visualizer

import visualizer.*
import java.awt.Dimension
import javax.swing.UIManager
import javax.swing.WindowConstants
import kotlin.swing.frame
import kotlin.swing.minimumHeight
import kotlin.swing.minimumWidth

/**
 * Created by igushs on 8/30/2015.
 */

class Demo() {

    val visualizer = Visualizer()

    val window = frame("Demo") {
        minimumHeight = 500
        minimumWidth = 500

        setContentPane(visualizer)
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    }

    fun start() {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        window.setVisible(true)
    }
}