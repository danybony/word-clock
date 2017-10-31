package net.bonysoft.wordclock.leds

import android.os.Build

private val DEVICE_EDISON = "edison"
private val DEVICE_RPI3 = "rpi3"
private val DEVICE_NXP_6 = "imx6ul"
private val DEVICE_NXP_7 = "imx7d_pico"

fun spiPort(): String {
    return when (Build.DEVICE) {
        DEVICE_EDISON -> "SPI2"
        DEVICE_RPI3 -> "SPI0.0"
        DEVICE_NXP_6 -> "SPI3_0"
        DEVICE_NXP_7 -> "SPI3.0"
        else -> throw IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE)
    }
}

