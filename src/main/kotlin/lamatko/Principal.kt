package lamatko

import lamatko.CandidateGenerator.gatherSolutions

object Principal {
    fun solve(
        codedText: String,
        digitDescription: String = guessDigitDescription(codedText),
        background: BackgroundProfile = BackgroundProfile.default,
        shuffleDigitOrder: Boolean = false,
        shuffleDigitCoding: Boolean = false,
        resultCount: Int = 100,
    ): List<Result> {
        val digits = digitDescription
            .trim()
            .split(" ")
            .map {
                Digit(
                    valueOrder = if (shuffleDigitCoding) Order.Unknown else Order.EitherEndian,
                    values = it.toList()
                )
            }
            .reversed()

        val code = codedText
            .trim()
            .split(" ")
            .map { it.reversed() }
            .map { it.toList() + List(digits.size - it.length, { ' ' }) }

        val problem = Problem(
            digitOrder = if (shuffleDigitOrder) Order.Unknown else Order.EitherEndian,
            digits = digits,
            alphabets = Alphabet.values().toList(),
            code = code,
            offsets = listOf(0, 1),
        )

        val results = problem.gatherSolutions(background)

        return results.take(resultCount)
    }

    fun guessDigitDescription(codedText: String): String {
        val codeUnits = codedText
            .trim()
            .split(" ")
            .map { it.reversed() }
            .filter { it.isNotBlank() }

        val digitCount: Int = codeUnits.map { it.length }.maxOrNull() ?: 1

        fun decodeDigit(n: Int, default: Char?): List<Char> {
            return codeUnits
                .mapNotNull { it.getOrNull(n) ?: default }
                .distinct()
                .sorted()
        }

        val zero = decodeDigit(0, null)[0]

        return (0 until digitCount)
            .map { decodeDigit(it, zero).joinToString ("") }
            .reversed()
            .joinToString(" ")
    }
}