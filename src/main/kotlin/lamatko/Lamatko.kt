package lamatko

import lamatko.CandidateGenerator.gatherSolutions
import lamatko.Lamatko.impl.simplify

object Lamatko {
    fun solve(
        codedText: String,
        digitDescription: String = guessDigitDescription(codedText),
        background: BackgroundProfile = BackgroundProfile.default,
        shuffleDigitOrder: Boolean = false,
        shuffleDigitCoding: Boolean = false,
        obscureAlphabets: Boolean = false,
        inheritDigitCoding: Boolean = false,
        sortResults: Boolean = true,
        resultCount: Int = 100,
        timeoutMillis: Long = 10000,
    ): List<Result> {
        val digits = digitDescription
            .split(" ")
            .map {
                Digit(
                    valueOrder =  when(shuffleDigitCoding to inheritDigitCoding) {
                        true to true -> Order.UnknownInherited
                        true to false -> Order.Unknown
                        false to true -> Order.EitherEndianInherited
                        false to false -> Order.EitherEndian
                        else -> Order.EitherEndian
                    },
                    values = it.toList()
                )
            }
            .reversed()

        val code = codedText
            .simplify()
            .split(" ")
            .map { it.reversed() }
            .map { it.toList() + List((digits.size - it.length).coerceAtLeast(0), { ' ' }) }

        val problem = Problem(
            digitOrder = when(shuffleDigitOrder) {
                true -> Order.Unknown
                else -> Order.EitherEndian
            },
            digits = digits,
            alphabets = Alphabet.values().toList().filter { !it.isObscure || obscureAlphabets },
            code = code,
            offsets = listOf(0, 1),
        )

        val results = problem.gatherSolutions(background, timeoutMillis, sortResults)

        return results.take(resultCount)
    }

    fun guessDigitDescription(codedText: String): String {
        val codeUnits = codedText
            .simplify()
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

    object impl {
        fun String.simplify(): String {
            return this
                .trim()
                .replace("[-,._;:\n]".toRegex(), " ")
                .replace("  *".toRegex(), " ")
        }
    }
}