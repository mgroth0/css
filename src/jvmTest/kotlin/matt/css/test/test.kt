package matt.css.test


import matt.css.rep.units.percent
import matt.css.ser.MarginSerializer
import matt.lang.assertions.require.requireEquals
import matt.test.Tests
import kotlin.test.Test

class CssTests: Tests() {
    @Test
    fun parsePercent() =
        assertRunsInOneSecond {
            requireEquals(
                MarginSerializer.fromString("1.0%"),
                1.0.percent
            )
        }
}
