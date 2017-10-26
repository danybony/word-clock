package net.bonysoft.wordclock.matrix

import net.bonysoft.wordclock.BuildConfig
import org.joda.time.LocalTime

interface MatrixGenerator {

    fun createMatrix(time: LocalTime): BooleanArray

    fun createErrorMatrix(): BooleanArray

}

class MatrixGeneratorFactory {

    fun createGenerator() : MatrixGenerator {
        val flavor = BuildConfig.FLAVOR
        return when {
            flavor.contains("italian", true) -> ItalianGenerator()
            flavor.contains("english", true) -> EnglishGenerator()
            else -> throw IllegalStateException("Unspecified matrix generator for app flavor $flavor")
        }
    }

}
