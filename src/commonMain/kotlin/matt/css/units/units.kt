package matt.css.units

import matt.css.props.VerticalAlign
import matt.lang.idea.PointIdea3
import matt.model.data.mathable.DoubleWrapper
import matt.model.data.percent.PercentIdea
import kotlin.jvm.JvmInline

//fun String.toPx(): Px {
//    if (isNotBlank()) requireContains(this, "px")
//    return replace("px", "").toInt().px
//}
//
//fun String.toPercent(): Percent {
//    if (isNotBlank()) requireContains(this, "%")
//    return replace("%", "").toInt().percent
//}
//
//fun String.toPxOrNullIfBlank(): Px? {
//    if (isNotBlank()) requireContains(this, "px")
//    return replace("px", "").toIntOrNullIfBlank()?.px
//}
//
//fun String.toPercentOrNullIfBlank(): Percent? {
//    if (isNotBlank()) requireContains(this, "%")
//    return replace("%", "").toIntOrNullIfBlank()?.percent
//}

interface CssValue {
    val css: String
}

sealed interface Margin : CssValue

object auto : Margin {
    override val css = this::class.simpleName!!
    override fun toString() = css
}

val Number.px get() = Px(this.toDouble())
val Int.percent get() = Percent(this)
val Double.percent get() = DoublePercent(this)


data class CssPoint(
    override val x: CssLength,
    override val y: CssLength
) : PointIdea3<CssLength>


sealed interface CssLength : CssValue


class Px(private val d: Double) : Margin, CssLength, VerticalAlign, TypedSvgLength<Px>, SvgFontSizeUnit { //NOSONAR

    companion object {
        const val UNIT = "px"
    }

    override val css = "${d}$UNIT"
    override fun toString() = css
    override fun fromDouble(d: Double): Px {
        return Px(d)
    }

    override val asDouble: Double
        get() = d

    operator fun plus(other: Int) = Px(d + other)
    operator fun minus(other: Int) = Px(d - other)
    operator fun times(other: Int) = Px(d * other)
    operator fun div(other: Int) = Px(d / other)
    operator fun times(other: Px) = Px(d * other.d)

    override val svgCode = "${d}$UNIT"

}

interface SvgUnit {
    val svgCode: String
}

interface SvgFontSizeUnit : SvgUnit {
    override val svgCode: String
}


typealias SvgLength = TypedSvgLength<*>

interface TypedSvgLength<L : TypedSvgLength<L>> : SvgUnit, DoubleWrapper<L> {
    override val svgCode: String
}


val Number.user get() = UserLength(this.toDouble())

class UserLength(user: Double) : TypedSvgLength<UserLength> {

    companion object {
        const val UNIT = ""
    }

    override val svgCode = "$user$UNIT"

    override fun fromDouble(d: Double): UserLength {
        return UserLength(d)
    }

    override val asDouble = user
}


val Number.pt get() = PointUnit(this.toDouble())

class PointUnit(user: Double) : SvgFontSizeUnit {
    override val svgCode = "$user"

}

val Number.vw get() = PercentViewportWidth(this.toDouble())

class PercentViewportWidth(vw: Double) : TypedSvgLength<PercentViewportWidth>, SvgFontSizeUnit, CssLength, Margin {
    companion object {
        const val UNIT = "vw"
    }

    override val svgCode = "${vw}$UNIT"

    override fun fromDouble(d: Double): PercentViewportWidth {
        return PercentViewportWidth(d)
    }

    override val asDouble = vw
    override val css = svgCode
}


val Number.vh get() = PercentViewportHeight(this.toDouble())

class PercentViewportHeight(vh: Double) : TypedSvgLength<PercentViewportHeight>, SvgFontSizeUnit, CssLength, Margin {

    companion object {
        const val UNIT = "vh"
    }

    override val svgCode = "${vh}$UNIT"
    override fun fromDouble(d: Double): PercentViewportHeight {
        return PercentViewportHeight(d)
    }

    override val asDouble = vh

    override val css = svgCode
}

val Number.vmin get() = PercentViewportSmallerDimension(this.toDouble())

class PercentViewportSmallerDimension(vmin: Double) : TypedSvgLength<PercentViewportSmallerDimension>, SvgFontSizeUnit,
    CssLength, Margin {

    companion object {
        const val UNIT = "vmin"
    }

    override val svgCode = "${vmin}$UNIT"

    override fun fromDouble(d: Double): PercentViewportSmallerDimension {
        return PercentViewportSmallerDimension(d)
    }

    override val asDouble = vmin
    override val css = svgCode
}

val Number.vmax get() = PercentViewportLargerDimension(this.toDouble())

class PercentViewportLargerDimension(vmax: Double) : TypedSvgLength<PercentViewportLargerDimension>, SvgFontSizeUnit,
    CssLength, Margin {

    companion object {
        const val UNIT = "vmax"
    }

    override val svgCode = "${vmax}$UNIT"
    override fun fromDouble(d: Double): PercentViewportLargerDimension {
        return PercentViewportLargerDimension(d)
    }

    override val asDouble = vmax
    override val css = svgCode
}


@JvmInline
value class Percent(internal val i: Int) : CssLength, VerticalAlign, PercentIdea, SvgFontSizeUnit { //NOSONAR

    companion object {
        const val UNIT = "%"
    }

    operator fun unaryMinus() = Percent(-i)
    override fun toString() = "${i}$UNIT"
    operator fun plus(other: Int) = Percent(i + other)
    operator fun minus(other: Int) = Percent(i - other)
    operator fun times(other: Int) = Percent(i * other)
    operator fun div(other: Int) = Percent(i / other)
    operator fun div(other: Double) = DoublePercent(i.toDouble() / other)
    operator fun times(other: Double) = DoublePercent(i.toDouble() * other)
    operator fun plus(other: Percent) = Percent(i + other.i)
    operator fun plus(other: DoublePercent) = DoublePercent(i.toDouble() + other.d)
    operator fun minus(other: DoublePercent) = DoublePercent(i.toDouble() - other.d)
    operator fun minus(other: Percent) = Percent(i - other.i)
    operator fun times(other: Percent) = Percent(i * other.i)
    operator fun div(other: Percent) = Percent(i / other.i)
    override val svgCode get() = "${i}$UNIT"
    override val css get() = svgCode

    fun toDoublePercent() = DoublePercent(i.toDouble())
}


@JvmInline
value class DoublePercent(internal val d: Double) : CssLength, VerticalAlign, PercentIdea,
    TypedSvgLength<DoublePercent>,
    SvgFontSizeUnit, Margin { //NOSONAR

    companion object {
        const val UNIT = Percent.UNIT
    }

    override fun toString() = css
    override val css get() = "${d}$UNIT"
    override fun fromDouble(d: Double): DoublePercent {
        return DoublePercent(d)
    }

    override val asDouble: Double
        get() = d


    /*
    Temporary workaround for https://youtrack.jetbrains.com/issue/KT-54513/K1-and-K2-java.lang.NoSuchMethodError-with-value-class-implementing-an-interface
    Once that bug is fixed, this can be deleted in favor of the default method from the interface
    * */
    override operator fun minus(m: DoublePercent) = super.minus(m)

    /*
    Temporary workaround for https://youtrack.jetbrains.com/issue/KT-54513/K1-and-K2-java.lang.NoSuchMethodError-with-value-class-implementing-an-interface
    Once that bug is fixed, this can be deleted in favor of the default method from the interface
    * */
    override operator fun plus(m: DoublePercent) = super.plus(m)

    operator fun plus(other: Int) = DoublePercent(d + other)
    operator fun minus(other: Int) = DoublePercent(d - other)
    operator fun times(other: Int) = DoublePercent(d * other)
    operator fun times(other: Double) = DoublePercent(d * other)
    operator fun div(other: Int) = DoublePercent(d / other)
    operator fun div(other: Double) = DoublePercent(d / other)
    operator fun minus(other: Percent) = DoublePercent(d - other.i)
    operator fun times(other: DoublePercent) = DoublePercent(d * other.d)
    override val svgCode get() = "${d}$UNIT"
}


//fun idk() {
//    DoublePercent(5.0) - 1.0
//}

