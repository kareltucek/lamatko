package lamatko

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