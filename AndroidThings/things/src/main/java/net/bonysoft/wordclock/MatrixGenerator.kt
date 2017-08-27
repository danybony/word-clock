package net.bonysoft.wordclock

import java.util.*

const val LED_SIZE = 144

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
        it < 10
    }
}

private fun BooleanArray.addHour(date: Date): BooleanArray {
    return this //TODO
}

private fun BooleanArray.addSeparator(date: Date): BooleanArray {
    return this //TODO
}

private fun BooleanArray.addMinutes(date: Date): BooleanArray {
    return this //TODO
}

