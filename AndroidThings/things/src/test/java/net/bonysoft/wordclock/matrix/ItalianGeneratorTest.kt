package net.bonysoft.wordclock.matrix

import org.joda.time.LocalTime
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertTrue

@RunWith(Parameterized::class)
class ItalianGeneratorTest(private val inputTime: LocalTime, private val displayedText: String) {

    private val LAYOUT = charArrayOf(
            'A', 'E', 'S', 'O', 'N', 'O', 'O', 'F', 'G', 'L', 'O', 'L',
            'W', 'R', 'G', 'H', 'X', 'L', 'U', 'N', 'A', 'O', 'V', 'O',
            'I', 'N', 'B', 'L', 'E', 'U', 'D', 'U', 'E', 'L', 'O', 'L',
            'F', 'C', 'I', 'N', 'Q', 'U', 'E', 'G', 'S', 'E', 'I', 'B',
            'I', 'D', 'I', 'E', 'C', 'I', 'D', 'O', 'D', 'I', 'C', 'I',
            'P', 'S', 'E', 'T', 'T', 'E', 'U', 'N', 'D', 'I', 'C', 'I',
            'A', 'O', 'T', 'T', 'O', 'N', 'O', 'V', 'E', 'O', 'C', 'F',
            'L', 'T', 'R', 'E', 'Q', 'U', 'A', 'T', 'T', 'R', 'O', 'L',
            'B', 'L', 'O', 'V', 'E', 'M', 'E', 'N', 'O', 'F', 'Q', 'D',
            'U', 'T', 'U', 'N', 'D', 'Q', 'U', 'A', 'R', 'T', 'O', 'I',
            'A', 'D', 'I', 'E', 'C', 'I', 'M', 'E', 'Z', 'Z', 'A', 'T',
            'V', 'E', 'N', 'T', 'I', 'C', 'I', 'N', 'Q', 'U', 'E', 'U'
    )

    private val matrixGenerator = ItalianGenerator()

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                    arrayOf(LocalTime(1, 0), "E l una"),
                    arrayOf(LocalTime(1, 2), "E l una"),
                    arrayOf(LocalTime(1, 4), "E l una"),
                    arrayOf(LocalTime(1, 5), "E l una e cinque"),
                    arrayOf(LocalTime(1, 7), "E l una e cinque"),
                    arrayOf(LocalTime(1, 9), "E l una e cinque"),
                    arrayOf(LocalTime(1, 10), "E l una e dieci"),
                    arrayOf(LocalTime(1, 14), "E l una e dieci"),
                    arrayOf(LocalTime(1, 15), "E l una e un quarto"),
                    arrayOf(LocalTime(1, 19), "E l una e un quarto"),
                    arrayOf(LocalTime(1, 20), "E l una e venti"),
                    arrayOf(LocalTime(1, 24), "E l una e venti"),
                    arrayOf(LocalTime(1, 25), "E l una e venticinque"),
                    arrayOf(LocalTime(1, 29), "E l una e venticinque"),
                    arrayOf(LocalTime(1, 30), "E l una e mezza"),
                    arrayOf(LocalTime(1, 34), "E l una e mezza"),
                    arrayOf(LocalTime(1, 35), "Sono le due meno venticinque"),
                    arrayOf(LocalTime(1, 39), "Sono le due meno venticinque"),
                    arrayOf(LocalTime(1, 40), "Sono le due meno venti"),
                    arrayOf(LocalTime(1, 44), "Sono le due meno venti"),
                    arrayOf(LocalTime(1, 45), "Sono le due meno un quarto"),
                    arrayOf(LocalTime(1, 49), "Sono le due meno un quarto"),
                    arrayOf(LocalTime(1, 50), "Sono le due meno dieci"),
                    arrayOf(LocalTime(1, 54), "Sono le due meno dieci"),
                    arrayOf(LocalTime(1, 55), "Sono le due meno cinque"),
                    arrayOf(LocalTime(1, 59), "Sono le due meno cinque"),
                    arrayOf(LocalTime(13, 0), "E l una"),
                    arrayOf(LocalTime(2, 0), "Sono le due"),
                    arrayOf(LocalTime(14, 0), "Sono le due"),
                    arrayOf(LocalTime(3, 0), "Sono le tre"),
                    arrayOf(LocalTime(15, 0), "Sono le tre"),
                    arrayOf(LocalTime(4, 0), "Sono le quattro"),
                    arrayOf(LocalTime(16, 0), "Sono le quattro"),
                    arrayOf(LocalTime(5, 0), "Sono le cinque"),
                    arrayOf(LocalTime(17, 0), "Sono le cinque"),
                    arrayOf(LocalTime(6, 0), "Sono le sei"),
                    arrayOf(LocalTime(18, 0), "Sono le sei"),
                    arrayOf(LocalTime(7, 0), "Sono le sette"),
                    arrayOf(LocalTime(19, 0), "Sono le sette"),
                    arrayOf(LocalTime(8, 0), "Sono le otto"),
                    arrayOf(LocalTime(20, 0), "Sono le otto"),
                    arrayOf(LocalTime(9, 0), "Sono le nove"),
                    arrayOf(LocalTime(21, 0), "Sono le nove"),
                    arrayOf(LocalTime(10, 0), "Sono le dieci"),
                    arrayOf(LocalTime(22, 0), "Sono le dieci"),
                    arrayOf(LocalTime(11, 0), "Sono le undici"),
                    arrayOf(LocalTime(23, 0), "Sono le undici"),
                    arrayOf(LocalTime(12, 0), "Sono le dodici"),
                    arrayOf(LocalTime(0, 0), "Sono le dodici")
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
