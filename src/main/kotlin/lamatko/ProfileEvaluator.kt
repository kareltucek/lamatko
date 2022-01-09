package lamatko

import lamatko.ProfileGenerator.impl.sliceBy

object ProfileEvaluator {
    fun BackgroundProfile.rate(str: String, debug: Boolean = false): Double = impl.rate(str, this, debug)

    object impl {
        fun rate(str: String, profile: BackgroundProfile, debug: Boolean): Double {
            if (debug) println("rating $str")
            return profile.probabilities
                .map {
                    val degree = it.degree
                    val probabilityMap = { arg1: String -> it.map.getOrDefault(arg1, it.defaultRating) }

                    val rating = str.sliceBy(degree)
                        .map(probabilityMap)
                        .sum()

                    if (debug) println("$degree $rating")

                    rating
                }
                .sum()
        }
    }
}