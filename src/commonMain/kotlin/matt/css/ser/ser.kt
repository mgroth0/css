package matt.css.ser

import matt.css.units.LengthUnit
import matt.css.units.Margin
import matt.css.units.SvgLength
import matt.css.units.auto
import matt.model.ser.EncodedAsStringSerializer
import kotlin.jvm.JvmName


object MarginSerializer : EncodedAsStringSerializer<Margin>() {
    override fun String.decode(): Margin =
        when (this) {
            auto.css -> auto
            else     -> SvgLengthSerializer.decode(this) as Margin
        }
    override fun Margin.encodeToString(): String = css
}


object SvgLengthSerializer : EncodedAsStringSerializer<SvgLength>() {


    override fun String.decode(): SvgLength = decode(this)


    @JvmName("decode2")
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

    override fun SvgLength.encodeToString(): String = svgCode
}
