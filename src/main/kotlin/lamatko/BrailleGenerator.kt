package lamatko

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