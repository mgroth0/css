package matt.css.test


import matt.css.ser.MarginCssConverter
import matt.css.units.percent
import matt.lang.assertions.require.requireEquals
import matt.test.Tests
import kotlin.test.Test

class CssTests: Tests() {
    @Test
    fun parsePercent() = assertRunsInOneSecond {
        requireEquals(
            MarginCssConverter.fromString("1.0%"),
            1.0.percent
        )
    }
}
