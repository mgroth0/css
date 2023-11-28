package matt.css.ser

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import matt.css.units.Margin
import matt.css.units.Percent
import matt.css.units.PercentViewportHeight
import matt.css.units.PercentViewportLargerDimension
import matt.css.units.PercentViewportSmallerDimension
import matt.css.units.PercentViewportWidth
import matt.css.units.Px
import matt.css.units.SvgLength
import matt.css.units.UserLength
import matt.css.units.auto
import matt.css.units.percent
import matt.css.units.px
import matt.css.units.user
import matt.css.units.vh
import matt.css.units.vmax
import matt.css.units.vmin
import matt.css.units.vw
import matt.prim.converters.StringConverter


object MarginSerializer : KSerializer<Margin> {

    override val descriptor = serialDescriptor<String>()

    override fun deserialize(decoder: Decoder): Margin {
        val d = decoder.decodeString()
        return MarginCssConverter.fromString(d)
    }

    override fun serialize(
        encoder: Encoder,
        value: Margin
    ) {
        encoder.encodeString(MarginCssConverter.toString(value))
    }

}


object MarginCssConverter : StringConverter<Margin> {
    override fun toString(t: Margin): String {
        return t.css
    }

    override fun fromString(s: String): Margin {
        return when (s) {
            auto.css -> auto
            else     -> SvgLengthSerializer.decode(s) as Margin
        }
    }
}


object SvgLengthSerializer : KSerializer<SvgLength> {
    override val descriptor = serialDescriptor<String>()


    override fun deserialize(decoder: Decoder): SvgLength {
        val string = decoder.decodeString()
        return decode(string)
    }

    fun decode(s: String): SvgLength {
        val trimmed = s.trim()
        val itr = trimmed.iterator()
        val num = StringBuilder()
        val unit = StringBuilder()
        while (itr.hasNext()) {
            val n = itr.next()
            when {
                n.isDigit() -> {
                    num.append(n)
                }

                n == '.'    -> {
                    num.append(n)
                }

                else        -> {
                    unit.append(n)
                    itr.forEach {
                        unit.append(it)
                    }
                    break
                }
            }
        }
        val number = num.toString().toDouble()
        return when (unit.toString()) {
            UserLength.UNIT                      -> number.user
            Px.UNIT                              -> number.px
            Percent.UNIT                         -> number.percent
            PercentViewportWidth.UNIT            -> number.vw
            PercentViewportHeight.UNIT           -> number.vh
            PercentViewportSmallerDimension.UNIT -> number.vmin
            PercentViewportLargerDimension.UNIT  -> number.vmax
            else                                 -> error("unknown SVG length unit: $unit, overall value is '${s}'")
        }
    }

    override fun serialize(
        encoder: Encoder,
        value: SvgLength
    ) {
        encoder.encodeString(value.svgCode)
    }

}