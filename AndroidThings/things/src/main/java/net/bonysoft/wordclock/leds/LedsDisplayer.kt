package net.bonysoft.wordclock.leds

import android.content.Context
import android.hardware.usb.UsbManager
import net.bonysoft.wordclock.BuildConfig
import net.bonysoft.wordclock.MatrixSerialiser
import net.bonysoft.wordclock.common.Configuration

interface LedsDisplayer {

    fun start()

    fun display(matrix: BooleanArray, configuration: Configuration)

    fun stop()

}

class LedsDisplayerFactory {

    fun createDisplayer(context: Context): LedsDisplayer {
        val flavor = BuildConfig.FLAVOR
        return when {
            flavor.contains("ws2801", true) -> Ws2801Displayer()
            flavor.contains("ws2812b", true) -> ArduinoConnectionDisplayer(
                    context.getSystemService<UsbManager>(UsbManager::class.java),
                    MatrixSerialiser()
            )
            else -> throw IllegalStateException("Unspecified LEDs displayer for app flavor $flavor")
        }
    }

}
