package com.navercorp.idea.plugin.maven.sdkhelper.service

import org.junit.Assert.assertEquals
import org.junit.Test

class LevenshteinSDKGuesserTest {

    @Test
    fun testDistance() {
        val guesser = LevenshteinSDKGuesser()
        assertEquals(guesser.getLevDistance("1.8", "zulu-1.8"), 5)
        assertEquals(guesser.getLevDistance("17", "zulu-1.8"), 7)
    }

}