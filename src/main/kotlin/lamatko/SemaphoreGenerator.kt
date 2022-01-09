package lamatko

import lamatko.BrailleGenerator.impl.contains

object SemaphoreGenerator {

    val letterCodes =
        listOf(56, 57, 58, 15, 25, 35, 45, 67, 68, 13, 16, 26, 36, 46, 78, 17, 27, 37, 47, 18, 28, 14, 23, 24, 38, 34)

    fun getSemaphore(charShift: Int, base: Int): List<String> {
        val result = Array<String>(base * base) { "?" }

        letterCodes.withIndex().forEach {
            val idx = it.index
            val code = it.value
            val letter = Alphabet.Standard.chars.get(idx)
            val codeA = code % 10 - charShift
            val codeB = code / 10 - charShift

            result[codeA * base + codeB] = letter
            result[codeA + codeB * base] = letter
        }

        return result.toList()
    }

}

object BrailleGenerator {
    fun getBraille2(): List<String> = impl.getTransformedBraille(impl.columnToRowLE)
    fun getBraille3(): List<String> = impl.getTransformedBraille(impl.columnToRowBE)

    object impl {
        val columnToRowLE = listOf(
            1 to 1,
            2 to 4,
            4 to 16,
            8 to 2,
            16 to 8,
            32 to 32,
        )
        val columnToRowBE = listOf(
            1 to 2,
            2 to 8,
            4 to 32,
            8 to 1,
            16 to 4,
            32 to 16
        )

        fun getTransformedBraille(translation: List<Pair<Int, Int>>): List<String> {

            val translatedAlphabet = Alphabet.BrailleCz.chars.withIndex().map {
                val char = it.value
                val idx = (it.index + 1)

                val translatedIdx = translation.map { (from, to) ->
                    if (idx.contains(from))
                        to
                    else
                        0
                }.sum()

                (translatedIdx - 1) to char
            }
                .sortedBy { it.first }
                .map { it.second }

            return translatedAlphabet
        }

        fun Int.contains(n: Int): Boolean {
            return (this % (n * 2)) / n != 0
        }
    }
}