package lamatko

object BrailleGenerator {

    fun getBraille(): List<String> = impl.getBaseBraille()
    fun getBraille2(): List<String> = impl.getTransformedBraille(impl.columnToRowLE)
    fun getBraille3(): List<String> = impl.getTransformedBraille(impl.columnToRowBE)

    object impl {
        val baseBrailleList = listOf(
            //common
            'a' to "1",
            'b' to "12",
            'c' to "14",
            'd' to "145",
            'e' to "15",
            'f' to "124",
            'g' to "1245",
            'h' to "125",
            'i' to "24",
            'j' to "245",
            'k' to "13",
            'l' to "123",
            'm' to "134",
            'n' to "1345",
            'o' to "135",
            'p' to "1234",
            'q' to "12345",
            'r' to "1235",
            's' to "234",
            't' to "2345",
            'u' to "136",
            'v' to "1236",
            'w' to "2456",
            'x' to "1346",
            'y' to "13456",
            'z' to "1356",
            //punctuation
            ',' to "2",
            ';' to "23",
            ':' to "25",
            '.' to "256",
            '?' to "236",
            '!' to "235",
            '‘' to "3",
            '–' to "36",
            // czech
            'a' to "16",
            'c' to "146",
            'd' to "1456",
            'e' to "345",
            'e' to "126",
            'i' to "34",
            'n' to "1246",
            'o' to "246",
            'r' to "2456",
            's' to "156",
            't' to "1256",
            'u' to "346",
            'u' to "23456",
            'y' to "12346",
            'z' to "2356",
        )

        val columnToValue = listOf(
            1 to 1,
            2 to 2,
            3 to 4,
            4 to 8,
            5 to 16,
            6 to 32,
        )

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

        fun getBaseBraille(): List<String> {
            val digitMap = columnToValue
                .toMap()
                .mapKeys { it.key.toString().first() }

           fun translateChar(brailleNum: String): Int {
               return brailleNum
                   .mapNotNull { digitMap[it] }
                   .sum()
           }

            val brailleMap = baseBrailleList
                .map { (translateChar(it.second)-1) to it.first.toString() }
                .toMap()

            return (0 .. 63)
                .map { brailleMap.getOrDefault(it, "?") }
        }

        fun getTransformedBraille(translation: List<Pair<Int, Int>>): List<String> {

            val translatedAlphabet = getBaseBraille().withIndex().map {
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