package com.navercorp.idea.plugin.maven.sdkhelper

class LevenshteinSDKGuesser: SDKGuesser {

    override fun guess(jdkVersion: String, jdkNames: List<String>): String? {
        var minDist = Int.MAX_VALUE
        var result: String? = null
        jdkNames.forEach { jdkName ->
            val dist = getLevDistance(jdkVersion, jdkName)
            println("distance($jdkVersion, $jdkName) = $dist")
            if (dist <= minDist) {
                minDist = dist
                result = jdkName
            }
        }
        return result
    }

    /**
     * <a href="https://en.wikipedia.org/wiki/Levenshtein_distance">Reference</a>
     */
    private fun getLevDistance(a: String, b: String): Int {
        val al = a.length
        val bl = b.length
        val mat = Array(al + 1) { IntArray(bl + 1) }

        for (i in 0..al) {
            mat[i][0] = i
            mat[0][i] = i
        }

        for (i in 1..al) {
            for (j in 1..bl) {
                val sub = if (a[i-1] == b[j-1]) 0 else 1
                mat[i][j] = minOf(
                    mat[i-1][j] + 1,
                    mat[i][j-1] + 1,
                    mat[i-1][j-1] + sub
                )
            }
        }

        return mat[al][bl]
    }

}