package net.bonysoft.wordclock

import org.joda.time.LocalTime
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertTrue

@RunWith(Parameterized::class)
class MatrixGeneratorTest(val inputTime: LocalTime, val displayedText: String) {

    private val LAYOUT = charArrayOf(
            'A', 'I', 'T', 'S', 'N', 'O', 'F', 'G', 'H', 'L', 'O', 'L',
            'W', 'R', 'H', 'A', 'L', 'F', 'T', 'E', 'N', 'O', 'V', 'O',
            'I', 'N', 'Q', 'U', 'A', 'R', 'T', 'E', 'R', 'L', 'O', 'L',
            'F', 'T', 'W', 'E', 'N', 'T', 'Y', 'F', 'I', 'V', 'E', 'C',
            'I', 'T', 'O', 'P', 'A', 'S', 'T', 'O', 'T', 'K', 'M', 'I',
            'P', 'S', 'O', 'N', 'E', 'T', 'W', 'O', 'N', 'S', 'C', 'L',
            'F', 'O', 'U', 'R', 'F', 'I', 'V', 'E', 'S', 'I', 'X', 'F',
            'U', 'E', 'L', 'E', 'V', 'E', 'N', 'T', 'H', 'R', 'E', 'E',
            'O', 'S', 'E', 'V', 'E', 'N', 'E', 'I', 'G', 'H', 'T', 'D',
            'L', 'T', 'W', 'E', 'L', 'V', 'E', 'T', 'E', 'N', 'C', 'I',
            'A', 'D', 'N', 'I', 'N', 'E', 'T', 'W', 'E', 'N', 'T', 'Y',
            'V', 'U', 'N', 'F', 'X', 'O', 'C', 'L', 'O', 'C', 'K', 'U'
    )

    private val matrixGenerator = MatrixGenerator()

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                    arrayOf(LocalTime(1, 0), "It s one o clock"),
                    arrayOf(LocalTime(1, 2), "It s one o clock"),
                    arrayOf(LocalTime(1, 4), "It s one o clock"),
                    arrayOf(LocalTime(1, 5), "It s five past one"),
                    arrayOf(LocalTime(1, 7), "It s five past one"),
                    arrayOf(LocalTime(1, 9), "It s five past one"),
                    arrayOf(LocalTime(1, 10), "It s ten past one"),
                    arrayOf(LocalTime(1, 14), "It s ten past one"),
                    arrayOf(LocalTime(1, 15), "It s quarter past one"),
                    arrayOf(LocalTime(1, 19), "It s quarter past one"),
                    arrayOf(LocalTime(1, 20), "It s twenty past one"),
                    arrayOf(LocalTime(1, 24), "It s twenty past one"),
                    arrayOf(LocalTime(1, 25), "It s twentyfive past one"),
                    arrayOf(LocalTime(1, 29), "It s twentyfive past one"),
                    arrayOf(LocalTime(1, 30), "It s half past one"),
                    arrayOf(LocalTime(1, 34), "It s half past one"),
                    arrayOf(LocalTime(1, 35), "It s twentyfive to two"),
                    arrayOf(LocalTime(1, 39), "It s twentyfive to two"),
                    arrayOf(LocalTime(1, 40), "It s twenty to two"),
                    arrayOf(LocalTime(1, 44), "It s twenty to two"),
                    arrayOf(LocalTime(1, 45), "It s quarter to two"),
                    arrayOf(LocalTime(1, 49), "It s quarter to two"),
                    arrayOf(LocalTime(1, 50), "It s ten to two"),
                    arrayOf(LocalTime(1, 54), "It s ten to two"),
                    arrayOf(LocalTime(1, 55), "It s five to two"),
                    arrayOf(LocalTime(1, 59), "It s five to two"),
                    arrayOf(LocalTime(13, 0), "It s one o clock"),
                    arrayOf(LocalTime(2, 0), "It s two o clock"),
                    arrayOf(LocalTime(14, 0), "It s two o clock"),
                    arrayOf(LocalTime(3, 0), "It s three o clock"),
                    arrayOf(LocalTime(15, 0), "It s three o clock"),
                    arrayOf(LocalTime(4, 0), "It s four o clock"),
                    arrayOf(LocalTime(16, 0), "It s four o clock"),
                    arrayOf(LocalTime(5, 0), "It s five o clock"),
                    arrayOf(LocalTime(17, 0), "It s five o clock"),
                    arrayOf(LocalTime(6, 0), "It s six o clock"),
                    arrayOf(LocalTime(18, 0), "It s six o clock"),
                    arrayOf(LocalTime(7, 0), "It s seven o clock"),
                    arrayOf(LocalTime(19, 0), "It s seven o clock"),
                    arrayOf(LocalTime(8, 0), "It s eight o clock"),
                    arrayOf(LocalTime(20, 0), "It s eight o clock"),
                    arrayOf(LocalTime(9, 0), "It s nine o clock"),
                    arrayOf(LocalTime(21, 0), "It s nine o clock"),
                    arrayOf(LocalTime(10, 0), "It s ten o clock"),
                    arrayOf(LocalTime(22, 0), "It s ten o clock"),
                    arrayOf(LocalTime(11, 0), "It s eleven o clock"),
                    arrayOf(LocalTime(23, 0), "It s eleven o clock"),
                    arrayOf(LocalTime(12, 0), "It s twelve o clock"),
                    arrayOf(LocalTime(0, 0), "It s twelve o clock")
            )
        }
    }

    @Test
    fun shouldTurnOnRightLettersForTime() {
        assertTrue("Should show \"$displayedText\" for ${inputTime.hourOfDay}:${inputTime.minuteOfHour}") {
            val matrix = matrixGenerator.createMatrix(inputTime)
            var resultCharIndex = 0
            matrix.forEachIndexed { i, isOn ->
                if (isOn) {
                    while (displayedText.toCharArray()[resultCharIndex] == ' ') {
                        resultCharIndex++
                    }
                    if (!LAYOUT[i].equals(displayedText.toCharArray()[resultCharIndex], true)) {
                        return@assertTrue false
                    }
                    resultCharIndex++
                }
            }
            return@assertTrue true
        }
    }

}
