package matt.css.ser

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import matt.css.units.LengthUnit
import matt.css.units.Margin
import matt.css.units.SvgLength
import matt.css.units.auto
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
    override fun toString(t: Margin): String = t.css

    override fun fromString(s: String): Margin = when (s) {
        auto.css -> auto
        else     -> SvgLengthSerializer.decode(s) as Margin
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
        return LengthUnit.unitFor(unit.toString()).of(number)
    }

    override fun serialize(
        encoder: Encoder,
        value: SvgLength
    ) {
        encoder.encodeString(value.svgCode)
    }

}
