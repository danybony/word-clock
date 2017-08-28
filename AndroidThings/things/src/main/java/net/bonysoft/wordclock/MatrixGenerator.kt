package net.bonysoft.wordclock

import java.util.*

const val LED_SIZE = 144

private val ITS = intArrayOf(1, 2, 3)
private val ONE = intArrayOf(62, 63, 64)
private val TWO = intArrayOf(65, 66, 67)

class MatrixGenerator {

    fun createMatrix(date: Date): BooleanArray {
        return BooleanArray(LED_SIZE, { false })
                .addPrefix(date)
                .addHour(date)
                .addSeparator(date)
                .addMinutes(date)
    }

}

private fun BooleanArray.addPrefix(date: Date): BooleanArray {
    return kotlin.BooleanArray(LED_SIZE) {
        ITS.contains(it)
    }
}

private fun BooleanArray.addHour(date: Date): BooleanArray {
    return kotlin.BooleanArray(LED_SIZE) {
        this[it] || ONE.contains(it)   //TODO
    }
}

private fun BooleanArray.addSeparator(date: Date): BooleanArray {
    return this //TODO
}

private fun BooleanArray.addMinutes(date: Date): BooleanArray {
    return this //TODO
}

