package net.bonysoft.wordclock

import org.joda.time.LocalTime

const val LED_SIZE = 144

private val ITS = intArrayOf(1, 2, 3)
private val ONE = intArrayOf(62, 63, 64)
private val TWO = intArrayOf(65, 66, 67)
private val THREE = intArrayOf(91, 92, 93, 94, 95)
private val FOUR = intArrayOf(72, 73, 74, 75)
private val FIVE = intArrayOf(76, 77, 78, 79)
private val SIX = intArrayOf(80, 81, 82)
private val SEVEN = intArrayOf(97, 98, 99, 100, 101)
private val EIGHT = intArrayOf(102, 103, 104, 105, 106)
private val NINE = intArrayOf(122, 123, 124, 125)
private val TEN = intArrayOf(115, 116, 117)
private val ELEVEN = intArrayOf(85, 86, 87, 88, 89, 90)
private val TWELVE = intArrayOf(109, 110, 111, 112, 113, 114)
private val MIN_FIVE = intArrayOf(43, 44, 45, 46)
private val MIN_TEN = intArrayOf(18, 19, 20)
private val MIN_QUARTER = intArrayOf(26, 27, 28, 29, 30, 31, 32)
private val MIN_TWENTY = intArrayOf(37, 38, 39, 40, 41, 42)
private val MIN_HALF = intArrayOf(14, 15, 16, 17)
private val TO = intArrayOf(49, 50)
private val PAST = intArrayOf(51, 52, 53, 54)
private val O_CLOCK = intArrayOf(137, 138, 139, 140, 141, 142)

class MatrixGenerator {

    fun createMatrix(time: LocalTime): BooleanArray {
        return BooleanArray(LED_SIZE, { false })
                .addPrefix(time)
                .addHour(time)
                .addSeparator(time)
                .addMinutes(time)
    }

}

private fun BooleanArray.addPrefix(time: LocalTime): BooleanArray {
    return kotlin.BooleanArray(LED_SIZE) {
        ITS.contains(it)
    }
}

private fun BooleanArray.addHour(time: LocalTime): BooleanArray {
    val displayedHour = if (time.minuteOfHour < 35) (time.hourOfDay % 12) else (time.hourOfDay + 1) % 12
    return kotlin.BooleanArray(LED_SIZE) {
        this[it] || when (displayedHour) {
            0 -> TWELVE.contains(it)
            1 -> ONE.contains(it)
            2 -> TWO.contains(it)
            3 -> THREE.contains(it)
            4 -> FOUR.contains(it)
            5 -> FIVE.contains(it)
            6 -> SIX.contains(it)
            7 -> SEVEN.contains(it)
            8 -> EIGHT.contains(it)
            9 -> NINE.contains(it)
            10 -> TEN.contains(it)
            11 -> ELEVEN.contains(it)
            else -> false
        }
    }
}

private fun BooleanArray.addSeparator(time: LocalTime): BooleanArray {
    val minutes = time.minuteOfHour
    return kotlin.BooleanArray(LED_SIZE) {
        this[it] || when {
            minutes < 5 -> false
            minutes < 35 -> PAST.contains(it)
            else -> TO.contains(it)
        }
    }
}

private fun BooleanArray.addMinutes(time: LocalTime): BooleanArray {
    val minutes = time.minuteOfHour
    return kotlin.BooleanArray(LED_SIZE) {
        this[it] || when {
            minutes < 5 -> O_CLOCK.contains(it)
            minutes < 10 -> MIN_FIVE.contains(it)
            minutes < 15 -> MIN_TEN.contains(it)
            minutes < 20 -> MIN_QUARTER.contains(it)
            minutes < 25 -> MIN_TWENTY.contains(it)
            minutes < 30 -> MIN_TWENTY.contains(it) || MIN_FIVE.contains(it)
            minutes < 35 -> MIN_HALF.contains(it)
            minutes < 40 -> MIN_TWENTY.contains(it) || MIN_FIVE.contains(it)
            minutes < 45 -> MIN_TWENTY.contains(it)
            minutes < 50 -> MIN_QUARTER.contains(it)
            minutes < 55 -> MIN_TEN.contains(it)
            minutes < 60 -> MIN_FIVE.contains(it)
            else -> false
        }
    }
}

