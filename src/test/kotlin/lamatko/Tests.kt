package lamatko

import lamatko.CandidateGenerator.gatherSolutions
import lamatko.ProfileEvaluator.rate
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class Tests {
    data class TestCase(
        val background: BackgroundProfile,
        val problem: Problem,
        val encodedString: String,
    )

    val background = BackgroundProfile.default

    val stringToBeCoded = "encodedstring"

    val decadicDigit = Digit(
        valueOrder = Order.LittleEndian,
        values = "0123456789".toList()
    )

    val base5Digit = Digit(
        valueOrder = Order.LittleEndian,
        values = "12345".toList()
    )

    val digitSubstitution = TestCase(
        encodedString = stringToBeCoded,
        background = background,
        problem = Problem(
            digitOrder = Order.EitherEndian,
            digits = listOf(decadicDigit, decadicDigit),
            alphabets = Alphabet.values().toList(),
            code = stringToBeCoded.map { it.code - 'a'.code + 1 }
                .map { it.toString() }
                .map { if (it.length == 1) "0$it" else it }
                .map { it.toList().reversed() },
            offsets = listOf(0, 1),
        )
    )

    val coordinates = TestCase(
        encodedString = "hrazmezidruhouatretiprehradouskale",
        background = background,
        problem = Problem(
            digitOrder = Order.EitherEndian,
            digits = listOf(base5Digit, base5Digit),
            alphabets = Alphabet.values().toList(),
            code = listOf(
                "32", "24", "11", "55", "33", "51", //|
                "55", "42", "41", "24", "54", "32", //|
                "53", "54", "11", "44", "24", "51", //|
                "44", "42", "14", "24", "51", "32", //|
                "24", "11", "41", "53", "54", "34", //|
                "13", "11", "23", "51"
            ).map { it.toList() },
            offsets = listOf(0, 1),
        )
    )

    fun runTest(test: TestCase) {
        val res = test.problem.gatherSolutions(background)

        println("----")
        res.take(10).map {
            println(it.describe())
        }

        Assertions.assertTrue(res[0].result == test.encodedString)
    }

    @Test
    fun testDigitDescription() {
        val digitGuess = Lamatko.guessDigitDescription("10 11 12 13 14 15 16 17 18 19 20 1")
        Assertions.assertEquals("012 0123456789", digitGuess)
    }

    @Test
    fun runDigitSubstitution() {
        runTest(coordinates)
    }

    @Test
    fun runBase5Coordinates() {
        runTest(digitSubstitution)
    }

    @Test
    fun runPrincipalOnThreeBase() {
        val all = Lamatko.solve(
            codedText = "202 012 201 202 202 012 202 120 102 200 1 201 112 012 201 100 20 200 221",
            digitDescription = "012 012 012",
            resultCount = 50,
        )

        Assertions.assertNotNull(all.find { it.result == "testtetokrasnesifry" })

        all.take(10).map { background.rate(it.result, debug = true)}
        all.take(10).map { println(it.describe()) }
    }

    /** tests:
     * - test that digits are picked up correctly
     * - digit order is shuffled
     * - coding of last digit is shuffled (by replacing 1 by A, which gives default order '02A')
     * */
    @Test
    fun runPincipalOnShuffledThreeBase() {
        val all = Lamatko.solve(
            codedText = "022 102 02A 022 022 102 022 210 012 020 00A 02A 112 102 02A 010 200 020 22A",
            resultCount = 50,
            shuffleDigitOrder = true,
            shuffleDigitCoding = true,
            inheritDigitCoding = true,
        )

        val res = all.find { it.result == "testtetokrasnesifry" }

        Assertions.assertNotNull(res)

        println(res!!.describe())
    }

    @Test
    fun runPrincipalOnSimpleSubstitution() {
        val res = Lamatko.solve(
            background = background,
            codedText = "21 17 00 19 19 4 18 4 13 00 18 08 11 13 08 02 08 00 09 03 04 19 04 19 04 03 14 11 04 21 00 15 14 18 8 11 13 08 02 08 03 14",
            digitDescription = "012 0123456789",
            resultCount = 1,
        ).first()

        Assertions.assertEquals("vrattesenasilniciajdetetedolevaposilnicido", res.result)

        println(res.describe())
    }


    @Test
    fun runPrincipalOnBraille() {
        /** Row encoded braille - littleEndian */
        val all = Lamatko.solve(
            background = background,
            codedText = "231 120 211 231",
            digitDescription = "0123 0123 0123",
            resultCount = 100000,
            shuffleDigitOrder = false,
            shuffleDigitCoding = false,
            obscureAlphabets = true,
        )

        Assertions.assertNotNull(all.find { it.result == "test" })
    }

    @Test
    fun runPrincipalOnBraille2() {
        /** Row encoded braille - bigEndian */
        val all = Lamatko.solve(
            background = background,
            codedText = "132 210 122 132",
            digitDescription = "0123 0123 0123",
            resultCount = 100000,
            shuffleDigitOrder = false,
            shuffleDigitCoding = true,
            obscureAlphabets = true,
        )

        Assertions.assertNotNull(all.find { it.result == "test" })
    }

    @Test
    fun runPrincipalOnSerialBraille2() {
        val all = Lamatko.solve(
            background = background,
            codedText = "1234 1235 1 134 15 1345 1345 24 234 13456 123 1 1236 24 14 13 1",
            digitDescription = "123456",
            resultCount = 100000,
            shuffleDigitOrder = true,
            shuffleDigitCoding = false,
            obscureAlphabets = true,
        )

        all.take(10).map { println(it.describe()) }

        Assertions.assertNotNull(all.find { it.result == "pramennisylavicka" })
    }


    @Test
    fun runPrincipalOnMobileCode() {
        val all = Lamatko.solve(
            background = background,
            codedText = "49 23 35 23 26 34 26 12",
            digitDescription = "1234 23456789",
            resultCount = 100000,
            shuffleDigitOrder = true,
            shuffleDigitCoding = false,
            obscureAlphabets = true,
        )

        Assertions.assertNotNull(all.find { it.result == "zelenina" })
    }

    @Test
    fun testOcrOutput() {
        val input = """
        32, 24, 11, 55, 33, 51, 55, 42, 41, 24, 54, 32, 53, 54,11,
        44, 24, 51, 44, 42, 14, 24, 51, 32, 24, 11, 41, 53, 54, 34.
        13, 11, 23, 51
        """

        val res = Lamatko.guessDigitDescription(input)

        Assertions.assertNotNull(res)
    }
}