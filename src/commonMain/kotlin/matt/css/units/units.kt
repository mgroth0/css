package matt.css.units

import matt.css.props.VerticalAlign
import matt.model.data.percent.PercentIdea
import matt.prim.str.toIntOrNullIfBlank
import kotlin.jvm.JvmInline

fun String.toPx(): Px {
    if (isNotBlank()) require("px" in this) { "px is not in $this" }
    return replace("px", "").toInt().px
}

fun String.toPercent(): Percent {
    if (isNotBlank()) require("%" in this) { "% is not in $this" }
    return replace("%", "").toInt().percent
}

fun String.toPxOrNullIfBlank(): Px? {
    if (isNotBlank()) require("px" in this) { "px is not in $this" }
    return replace("px", "").toIntOrNullIfBlank()?.px
}

fun String.toPercentOrNullIfBlank(): Percent? {
    if (isNotBlank()) require("%" in this) { "% is not in $this" }
    return replace("%", "").toIntOrNullIfBlank()?.percent
}


sealed interface Margin

object auto : Margin {
    override fun toString() = this::class.simpleName!!
}

val Int.px get() = Px(this)
val Int.percent get() = Percent(this)


sealed interface Length


class Px(private val i: Int) : Margin, Length, VerticalAlign { //NOSONAR
    operator fun unaryMinus() = Px(-i)
    override fun toString() = "${i}px"
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


