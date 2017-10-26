package net.bonysoft.wordclock.matrix

import org.joda.time.LocalTime

class ItalianGenerator : MatrixGenerator {

    companion object {

        const val LED_SIZE = 144

        private val SONO = intArrayOf(2, 3, 4, 5)
        private val E_ = intArrayOf(1)
        private val LE = intArrayOf(27, 28)
        private val L_ = intArrayOf(17)
        private val UNA = intArrayOf(18, 19, 20)
        private val DUE = intArrayOf(30, 31, 32)
        private val TRE = intArrayOf(85, 86, 87)
        private val QUATTRO = intArrayOf(88, 89, 90, 91, 92, 93)
        private val CINQUE = intArrayOf(37, 38, 39, 40, 41, 42)
        private val SEI = intArrayOf(44, 45, 46)
        private val SETTE = intArrayOf(61, 62, 63, 64, 65)
        private val OTTO = intArrayOf(73, 74, 75, 76)
        private val NOVE = intArrayOf(77, 78, 79, 80)
        private val DIECI = intArrayOf(49, 50, 51, 52, 53)
        private val UNDICI = intArrayOf(66, 67, 68, 69, 70, 71)
        private val DODICI = intArrayOf(54, 55, 56, 57, 58, 59)
        private val MIN_CINQUE = intArrayOf(137, 138, 139, 140, 141, 142)
        private val MIN_DIECI = intArrayOf(121, 122, 123, 124, 125)
        private val MIN_UN_QUARTO = intArrayOf(110, 111, 113, 114, 115, 116, 117, 118)
        private val MIN_VENTI = intArrayOf(132, 133, 134, 135, 136)
        private val MIN_MEZZA = intArrayOf(126, 127, 128, 129, 130)
        private val MENO = intArrayOf(101, 102, 103, 104)
        private val E = intArrayOf(102)
        private val ERROR = intArrayOf(14, 15, 20, 21, 25, 26, 27, 28, 31, 32, 33, 34, 38, 39, 44, 45, 88, 89, 90, 91, 99, 100, 101, 102, 103, 104, 110, 111, 116, 117, 121, 122, 129, 130)

    }

    override fun createMatrix(time: LocalTime): BooleanArray {
        return BooleanArray(LED_SIZE, { false })
                .addPrefix(time)
                .addHour(time)
                .addSeparator(time)
                .addMinutes(time)
    }

    override fun createErrorMatrix(): BooleanArray {
        return BooleanArray(LED_SIZE, { ERROR.contains(it) })
    }

    private fun BooleanArray.addPrefix(time: LocalTime): BooleanArray {
        val displayedHour = if (time.minuteOfHour < 35) (time.hourOfDay % 12) else (time.hourOfDay + 1) % 12
        return kotlin.BooleanArray(LED_SIZE) {
            return@BooleanArray if (displayedHour == 1) {
                E_.contains(it) || L_.contains(it)
            } else {
                SONO.contains(it) || LE.contains(it)
            }
        }
    }

    private fun BooleanArray.addHour(time: LocalTime): BooleanArray {
        val displayedHour = if (time.minuteOfHour < 35) (time.hourOfDay % 12) else (time.hourOfDay + 1) % 12
        return kotlin.BooleanArray(LED_SIZE) {
            this[it] || when (displayedHour) {
                0 -> DODICI.contains(it)
                1 -> UNA.contains(it)
                2 -> DUE.contains(it)
                3 -> TRE.contains(it)
                4 -> QUATTRO.contains(it)
                5 -> CINQUE.contains(it)
                6 -> SEI.contains(it)
                7 -> SETTE.contains(it)
                8 -> OTTO.contains(it)
                9 -> NOVE.contains(it)
                10 -> DIECI.contains(it)
                11 -> UNDICI.contains(it)
                else -> false
            }
        }
    }

    private fun BooleanArray.addSeparator(time: LocalTime): BooleanArray {
        val minutes = time.minuteOfHour
        return kotlin.BooleanArray(LED_SIZE) {
            this[it] || when {
                minutes < 5 -> false
                minutes < 35 -> E.contains(it)
                else -> MENO.contains(it)
            }
        }
    }

    private fun BooleanArray.addMinutes(time: LocalTime): BooleanArray {
        val minutes = time.minuteOfHour
        return kotlin.BooleanArray(LED_SIZE) {
            this[it] || when {
                minutes < 5 -> false
                minutes < 10 -> MIN_CINQUE.contains(it)
                minutes < 15 -> MIN_DIECI.contains(it)
                minutes < 20 -> MIN_UN_QUARTO.contains(it)
                minutes < 25 -> MIN_VENTI.contains(it)
                minutes < 30 -> MIN_VENTI.contains(it) || MIN_CINQUE.contains(it)
                minutes < 35 -> MIN_MEZZA.contains(it)
                minutes < 40 -> MIN_VENTI.contains(it) || MIN_CINQUE.contains(it)
                minutes < 45 -> MIN_VENTI.contains(it)
                minutes < 50 -> MIN_UN_QUARTO.contains(it)
                minutes < 55 -> MIN_DIECI.contains(it)
                minutes < 60 -> MIN_CINQUE.contains(it)
                else -> false
            }
        }
    }

}
