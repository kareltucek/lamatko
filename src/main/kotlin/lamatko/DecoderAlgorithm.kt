package lamatko

import lamatko.ProfileEvaluator.rate

object DecoderAlgorithm {
    fun Decoder.decode(profile: BackgroundProfile): Result = impl.decode(this, profile)

    fun List<Decoder>.decode(profile: BackgroundProfile): List<Result> = this.mapNotNull {
        try { it.decode(profile) }
        catch (e: Throwable) { null }
    }

    object impl {
        fun decode(decoder: Decoder, profile: BackgroundProfile): Result {
            return decoder.code
                .map { it.decodeChar(decoder) }
                .joinToString("")
                .let { decodedString ->
                    Result(
                        result = decodedString,
                        decoder = decoder,
                        rating = profile.rate(decodedString)
                    )
                }
        }

        fun List<Char>.decodeChar ( decoder: Decoder): String {
            return decoder.digitTable
                .zip(this)
                .map { (digitTable, char) -> digitTable[char] ?: 0 }
                .sum()
                .let { decoder.alphabet.chars.getOrNull (it - decoder.startOffset) ?: "?" }
        }
    }
}