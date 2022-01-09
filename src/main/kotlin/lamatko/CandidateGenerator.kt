package lamatko

import lamatko.DecoderAlgorithm.decode
import java.util.*


object CandidateGenerator {
    fun Problem.gatherSolutions(
        profile: BackgroundProfile = BackgroundProfile.default,
        timeout: Long = 10000
    ): List<Result> {
        val dict: MutableMap<String, Result> = mutableMapOf()

        val timeoutAt = System.currentTimeMillis() + timeout

        impl.generateCandidates(this) {
            val res = it.decode(profile)
            if (!dict.containsKey(res.result)) {
                dict.put(res.result, res)
            }
            if (System.currentTimeMillis() > timeoutAt) {
                throw Throwable("Timeout reached!")
            }
        }

        return dict.values.sortedByDescending { it.rating }
    }

    fun Problem.gatherCandidates(): Set<Decoder> {
        val res: MutableSet<Decoder> = mutableSetOf()

        impl.generateCandidates(this) {
            res.add(it)
        }

        return res
    }

    object impl {
        fun generateCandidates(problem: Problem, f: (Decoder) -> Unit): Unit {
            problem.digits.indices.toList().forEachPermutation(problem.digitOrder) { digitOrder ->
                problem.alphabets.forEach { alphabet ->
                    problem.offsets.forEach { offset ->
                        problem.digits.forEachCoding { coding ->
                            f(
                                Decoder(
                                    priority = 0,
                                    code = problem.code,
                                    digitTable = createDigitMap(coding, digitOrder),
                                    alphabet = alphabet,
                                    startOffset = offset,
                                )
                            )
                        }
                    }
                }
            }
        }

        fun createDigitMap(digits: List<List<Char>>, digitOrder: List<Int>): List<Map<Char, Int>> {
            val magnitudes = digitOrder.fold(1 to listOf<Pair<Int, Int>>()) { state, idx ->
                (state.first * digits[idx].size) to state.second + listOf(idx to state.first)
            }
                .second
                .sortedBy { it.first }
                .map { it.second }

            return magnitudes
                .zip(digits)
                .map { (magnitude, charList) ->
                    charList
                        .withIndex()
                        .map { it.value to (magnitude * it.index) }
                        .toMap()
                }
        }

        fun <T> List<T>.forEachPermutation(order: Order, f: (List<T>) -> Unit): Unit {
            if (!order.others) {
                if (order.littleEndian) {
                    f(this)
                }
                if (order.bigEndian) {
                    f(this.reversed())
                }
            } else {
                this.forEachPermutation(listOf(), f)
            }
        }

        fun <T> List<T>.forEachPermutation(prefix: List<T>, f: (List<T>) -> Unit): Unit {
            if (this.size <= 1) {
                f(prefix + this)
            } else {
                this.forEach { elem ->
                    this
                        .filter { it != elem }
                        .forEachPermutation(prefix + elem, f)
                }
            }
        }

        fun List<Digit>.forEachCoding(prefix: List<List<Char>> = emptyList(), f: (List<List<Char>>) -> Unit): Unit {
            if (this.isEmpty()) {
                f(prefix)
            } else {
                val head = this.first()
                val tail = this.drop(1)
                head.values.forEachPermutation(head.valueOrder) { permutation ->
                    tail.forEachCoding(prefix + listOf(permutation), f)
                }
            }
        }
    }
}


