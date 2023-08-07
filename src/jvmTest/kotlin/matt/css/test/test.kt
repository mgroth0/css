package matt.css.test


import matt.css.units.percent
import matt.css.units.toPercent
import matt.lang.require.requireEquals
import matt.test.JupiterTestAssertions.assertRunsInOneSecond
import kotlin.test.Test

class CssTests() {
    @Test
    fun parsePercent() = assertRunsInOneSecond {
        requireEquals("1%".toPercent(), 1.percent)
    }
}