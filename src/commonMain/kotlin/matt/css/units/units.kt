package matt.css.units

import matt.css.props.VerticalAlign
import matt.lang.require.requireContains
import matt.model.data.percent.PercentIdea
import matt.model.op.convert.StringConverter
import matt.prim.str.toIntOrNullIfBlank
import kotlin.jvm.JvmInline

fun String.toPx(): Px {
    if (isNotBlank()) requireContains(this, "px")
    return replace("px", "").toInt().px
}

fun String.toPercent(): Percent {
    if (isNotBlank()) requireContains(this, "%")
    return replace("%", "").toInt().percent
}

fun String.toPxOrNullIfBlank(): Px? {
    if (isNotBlank()) requireContains(this, "px")
    return replace("px", "").toIntOrNullIfBlank()?.px
}

fun String.toPercentOrNullIfBlank(): Percent? {
    if (isNotBlank()) requireContains(this, "%")
    return replace("%", "").toIntOrNullIfBlank()?.percent
}


sealed interface Margin {
    val css: String
}

object MarginCssConverter : StringConverter<Margin> {
    override fun toString(t: Margin): String {
        return t.css
    }

    override fun fromString(s: String): Margin {
        return if (s == auto::class.simpleName!!) auto else s.toPx()
    }

}

object auto : Margin {
    override val css = this::class.simpleName!!
    override fun toString() = css
}

val Int.px get() = Px(this)
val Int.percent get() = Percent(this)


sealed interface Length


class Px(private val i: Int) : Margin, Length, VerticalAlign { //NOSONAR
    operator fun unaryMinus() = Px(-i)
    override val css = "${i}px"
    override fun toString() = css
    operator fun plus(other: Int) = Px(i + other)
    operator fun minus(other: Int) = Px(i - other)
    operator fun times(other: Int) = Px(i * other)
    operator fun div(other: Int) = Px(i / other)
    operator fun plus(other: Px) = Px(i + other.i)
    operator fun minus(other: Px) = Px(i - other.i)
    operator fun times(other: Px) = Px(i * other.i)
    operator fun div(other: Px) = Px(i / other.i)

}

@JvmInline
value class Percent(private val i: Int) : Length, VerticalAlign, PercentIdea { //NOSONAR
    operator fun unaryMinus() = Percent(-i)
    override fun toString() = "${i}%"
    operator fun plus(other: Int) = Percent(i + other)
    operator fun minus(other: Int) = Percent(i - other)
    operator fun times(other: Int) = Percent(i * other)
    operator fun div(other: Int) = Percent(i / other)
    operator fun plus(other: Percent) = Percent(i + other.i)
    operator fun minus(other: Percent) = Percent(i - other.i)
    operator fun times(other: Percent) = Percent(i * other.i)
    operator fun div(other: Percent) = Percent(i / other.i)
}


@JvmInline
value class DoublePercent(private val d: Double) : Length, VerticalAlign, PercentIdea { //NOSONAR
    operator fun unaryMinus() = DoublePercent(-d)
    override fun toString() = "${d}%"
    operator fun plus(other: Int) = DoublePercent(d + other)
    operator fun minus(other: Int) = DoublePercent(d - other)
    operator fun times(other: Int) = DoublePercent(d * other)
    operator fun div(other: Int) = DoublePercent(d / other)
    operator fun plus(other: DoublePercent) = DoublePercent(d + other.d)
    operator fun minus(other: DoublePercent) = DoublePercent(d - other.d)
    operator fun times(other: DoublePercent) = DoublePercent(d * other.d)
    operator fun div(other: DoublePercent) = DoublePercent(d / other.d)
}



