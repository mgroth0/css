package matt.css.units

import kotlinx.serialization.json.internal.FormatLanguage
import matt.css.props.VerticalAlign
import matt.lang.idea.PointIdea3
import matt.model.data.mathable.DoubleWrapper
import matt.model.data.mathable.IntWrapper
import matt.model.data.mathable.NumberWrapper
import matt.model.data.percent.PercentIdea

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


sealed interface CssLength : CssValue {
    val unit: LengthUnit
}

interface DoubleCssLength<M : DoubleWrapper<M>> : CssLength, DoubleWrapper<M>
interface IntCssLength<M : IntWrapper<M>> : CssLength, IntWrapper<M>

sealed class JustSvgLengthBase<N : NumberWrapper<N>> : SvgUnit, NumberWrapper<N> {
    abstract val unit: LengthUnit
    final override val svgCode get() = "${asNumber}${unit.abbreviation}"
}

sealed class CssLengthBaseBase<N : NumberWrapper<N>> : CssLength, NumberWrapper<N> {
    final override val css get() = "${asNumber}${unit.abbreviation}"
}

sealed class CssIntLengthBase<M : IntWrapper<M>> : CssLengthBaseBase<M>(), IntCssLength<M>

sealed class CssLengthBase<M : DoubleWrapper<M>> : CssLengthBaseBase<M>(), DoubleCssLength<M>

sealed class SvgAndCssLengthBase<M : DoubleWrapper<M>> : CssLengthBase<M>(), SvgUnit {
    final override val svgCode: String get() = css
}


class Px(private val d: Double) : SvgAndCssLengthBase<Px>(), Margin, VerticalAlign, TypedSvgLength<Px>,
    SvgFontSizeUnit { //NOSONAR

    override val unit = LengthUnit.Px

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


}

interface SvgUnit {
    @get:FormatLanguage("SVG", prefix = "<svg><rect height=\"", suffix = "\"></svg>")
    val svgCode: String
}

interface SvgFontSizeUnit : SvgUnit {
    @get:FormatLanguage("SVG", prefix = "<svg><rect height=\"", suffix = "\"></svg>")
    override val svgCode: String
}


typealias SvgLength = TypedSvgLength<*>

interface TypedSvgLength<L : TypedSvgLength<L>> : SvgUnit, DoubleWrapper<L>


val Number.user get() = UserLength(this.toDouble())


class UserLength(user: Double) : JustSvgLengthBase<UserLength>(), TypedSvgLength<UserLength> {


    override val unit = LengthUnit.User
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

class PercentViewportWidth(vw: Double) : SvgAndCssLengthBase<PercentViewportWidth>(),
    TypedSvgLength<PercentViewportWidth>, SvgFontSizeUnit, Margin {

    override val unit = LengthUnit.PercentViewportWidth
    override fun fromDouble(d: Double): PercentViewportWidth {
        return PercentViewportWidth(d)
    }

    override val asDouble = vw
}


val Number.vh get() = PercentViewportHeight(this.toDouble())

class PercentViewportHeight(vh: Double) : SvgAndCssLengthBase<PercentViewportHeight>(),
    TypedSvgLength<PercentViewportHeight>, SvgFontSizeUnit, Margin {

    override val unit = LengthUnit.PercentViewportHeight
    override fun fromDouble(d: Double): PercentViewportHeight {
        return PercentViewportHeight(d)
    }

    override val asDouble = vh

}

val Number.vmin get() = PercentViewportSmallerDimension(this.toDouble())

class PercentViewportSmallerDimension(vmin: Double) : SvgAndCssLengthBase<PercentViewportSmallerDimension>(),
    TypedSvgLength<PercentViewportSmallerDimension>, SvgFontSizeUnit, Margin {

    override val unit = LengthUnit.PercentViewportSmallerDimension
    override fun fromDouble(d: Double): PercentViewportSmallerDimension {
        return PercentViewportSmallerDimension(d)
    }

    override val asDouble = vmin
}

val Number.vmax get() = PercentViewportLargerDimension(this.toDouble())

class PercentViewportLargerDimension(vmax: Double) : SvgAndCssLengthBase<PercentViewportLargerDimension>(),
    TypedSvgLength<PercentViewportLargerDimension>, SvgFontSizeUnit,
    CssLength, Margin {

    override val unit = LengthUnit.PercentViewportLargerDimension
    override fun fromDouble(d: Double): PercentViewportLargerDimension {
        return PercentViewportLargerDimension(d)
    }

    override val asDouble = vmax
}


data class Percent(internal val i: Int) : CssIntLengthBase<Percent>(), VerticalAlign, PercentIdea,
    SvgFontSizeUnit { //NOSONAR

    override val unit = LengthUnit.Percent


    override val asInt: Int
        get() = i

    override fun fromInt(d: Int): Percent {
        return Percent(d)
    }


    override val svgCode: String
        get() = css


    //    operator fun unaryMinus() = Percent(-i)
    override fun toString() = css
    operator fun plus(other: Int) = Percent(i + other)
    operator fun minus(other: Int) = Percent(i - other)
    operator fun times(other: Int) = Percent(i * other)
    operator fun div(other: Int) = Percent(i / other)
    operator fun div(other: Double) = DoublePercent(i.toDouble() / other)
    operator fun times(other: Double) = DoublePercent(i.toDouble() * other)

    //    operator fun plus(other: Percent) = Percent(i + other.i)
    operator fun plus(other: DoublePercent) = DoublePercent(i.toDouble() + other.d)
    operator fun minus(other: DoublePercent) = DoublePercent(i.toDouble() - other.d)

    //    operator fun minus(other: Percent) = Percent(i - other.i)
    operator fun times(other: Percent) = Percent(i * other.i)
//    operator fun div(other: Percent) = Percent(i / other.i)

    fun toDoublePercent() = DoublePercent(i.toDouble())
}


data class DoublePercent(internal val d: Double) : SvgAndCssLengthBase<DoublePercent>(), CssLength, VerticalAlign,
    PercentIdea,
    TypedSvgLength<DoublePercent>, SvgFontSizeUnit, Margin { //NOSONAR


    override val unit = LengthUnit.Percent

    override fun toString() = css
    override fun fromDouble(d: Double): DoublePercent {
        return DoublePercent(d)
    }

    override val asDouble: Double
        get() = d


    /*
    Temporary workaround for https://youtrack.jetbrains.com/issue/KT-54513/K1-and-K2-java.lang.NoSuchMethodError-with-value-class-implementing-an-interface
    Once that bug is fixed, this can be deleted in favor of the default method from the interface
    * */
    /*override operator fun minus(m: DoublePercent) = super.minus(m)*/

    /*
    Temporary workaround for https://youtrack.jetbrains.com/issue/KT-54513/K1-and-K2-java.lang.NoSuchMethodError-with-value-class-implementing-an-interface
    Once that bug is fixed, this can be deleted in favor of the default method from the interface
    * */
    /*override operator fun plus(m: DoublePercent) = super.plus(m)*/

    operator fun plus(other: Int) = DoublePercent(d + other)
    operator fun minus(other: Int) = DoublePercent(d - other)
    operator fun times(other: Int) = DoublePercent(d * other)
    operator fun times(other: Double) = DoublePercent(d * other)
    operator fun div(other: Int) = DoublePercent(d / other)
    operator fun div(other: Double) = DoublePercent(d / other)
    operator fun minus(other: Percent) = DoublePercent(d - other.i)
    operator fun times(other: DoublePercent) = DoublePercent(d * other.d)
}


//fun idk() {
//    DoublePercent(5.0) - 1.0
//}


enum class LengthUnit(val abbreviation: String) {
    User(""),
    Px("px"),
    Percent("%"),
    PercentViewportWidth("vw"),
    PercentViewportHeight("vh"),
    PercentViewportSmallerDimension("vmin"),
    PercentViewportLargerDimension("vmax");


    companion object {
        fun unitFor(abbreviation: String) = entries.first { it.abbreviation == abbreviation }
    }

    fun of(value: Double) = when (this) {
        User                            -> value.user
        Px                              -> value.px
        Percent                         -> value.percent
        PercentViewportWidth            -> value.vw
        PercentViewportHeight           -> value.vh
        PercentViewportSmallerDimension -> value.vmin
        PercentViewportLargerDimension  -> value.vmax
    }


}
