package net.bonysoft.wordclock

const val LED_SEPARATOR = ','
const val START = "/"
const val END = '!'

class MatrixSerialiser {

    fun serialise(matrix: BooleanArray): String {
        val stringBuilder = StringBuilder(START)
        matrix.forEachIndexed { index, isOn ->
            if (isOn) {
                stringBuilder.append(index.toString()).append(LED_SEPARATOR)
            }
        }
        stringBuilder.append(END)
        return stringBuilder.toString()
    }

}
