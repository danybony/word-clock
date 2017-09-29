package net.bonysoft.wordclock

import net.bonysoft.wordclock.common.Configuration

const val LED_SEPARATOR = ','
const val START = "/"
const val RED = "r"
const val GREEN = "g"
const val BLUE = "b"
const val END = '!'

class MatrixSerialiser {

    fun serialise(matrix: BooleanArray, configuration: Configuration): String {
        val r = configuration.color shr 16 and 0xFF
        val g = configuration.color shr 8 and 0xFF
        val b = configuration.color shr 0 and 0xFF
        val stringBuilder = StringBuilder(START)
                .append(r).append(RED)
                .append(g).append(GREEN)
                .append(b).append(BLUE)
        matrix.forEachIndexed { index, isOn ->
            if (isOn) {
                stringBuilder.append(index.toString()).append(LED_SEPARATOR)
            }
        }
        stringBuilder.append(END)
        return stringBuilder.toString()
    }

}
