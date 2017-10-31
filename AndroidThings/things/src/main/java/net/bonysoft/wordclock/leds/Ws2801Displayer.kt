package net.bonysoft.wordclock.leds

import android.graphics.Color
import com.xrigau.driver.ws2801.Ws2801
import net.bonysoft.wordclock.common.Configuration

class Ws2801Displayer : LedsDisplayer {

    private lateinit var ledstrip: Ws2801

    override fun start() {
        ledstrip = Ws2801.create(spiPort(), Ws2801.Mode.RBG)
    }

    override fun display(matrix: BooleanArray, configuration: Configuration) {
        val colors = IntArray(3) {
            return@IntArray if (matrix[it]) configuration.color else Color.BLACK
        }
        ledstrip.write(colors)
    }

    override fun stop() {
        ledstrip.close()
    }
}
