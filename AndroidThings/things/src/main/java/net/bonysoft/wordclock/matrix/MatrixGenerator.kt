package net.bonysoft.wordclock.matrix

import org.joda.time.LocalTime

interface MatrixGenerator {

    fun createMatrix(time: LocalTime): BooleanArray

    fun createErrorMatrix(): BooleanArray

}
