package matt.css.write

import kotlinx.css.PointerEvents
import kotlinx.css.Visibility
import kotlinx.html.CommonAttributeGroupFacade
import kotlinx.html.style
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import matt.color.common.ColorLike
import matt.css.deHyphenize
import matt.css.hyphenize
import matt.css.rep.props.AlignItems
import matt.css.rep.props.BorderStyle
import matt.css.rep.props.BorderWidth
import matt.css.rep.props.BoxSizing
import matt.css.rep.props.ColorLikeCssConverter
import matt.css.rep.props.Cursor
import matt.css.rep.props.Display
import matt.css.rep.props.FlexDirection
import matt.css.rep.props.Float
import matt.css.rep.props.FontStyle
import matt.css.rep.props.FontWeight
import matt.css.rep.props.JustifyContent
import matt.css.rep.props.Outline
import matt.css.rep.props.Overflow
import matt.css.rep.props.Position
import matt.css.rep.props.TextAlign
import matt.css.rep.props.VerticalAlign
import matt.css.rep.props.VerticalAligns
import matt.css.rep.props.WhiteSpace
import matt.css.rep.transform.Transform
import matt.css.rep.units.CssLength
import matt.css.rep.units.Margin
import matt.css.rep.units.Px
import matt.css.ser.MarginSerializer
import matt.lang.idea.PointIdea2
import matt.model.op.convert.StringStringConverter
import matt.prim.converters.StringConverter
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

val CommonAttributeGroupFacade.sty get() = HTMLDslStyleDSL(this)
fun CommonAttributeGroupFacade.sty(op: CssStyleDSL.() -> Unit) = HTMLDslStyleDSL(this).op()
class HTMLDslStyleDSL(val tag: CommonAttributeGroupFacade) : CssStyleDSL() {


    companion object {
        private const val STYLE_KEY = "style"
    }

    override operator fun set(
        key: String,
        value: Any
    ) {
        if (STYLE_KEY !in tag.attributes) {
            tag.style = "$key: $value;"
        } else {
            tag.style += " $key: $value;"
        }
    }

    override fun get(key: String): String = TODO()

    override fun remove(key: String) = TODO()

    override fun clear() = TODO()
}

@DslMarker
annotation class StyleDSLMarker

@StyleDSLMarker
abstract class MyStyleDsl {

    abstract operator fun set(
        key: String,
        value: Any
    )

    abstract operator fun get(key: String): String
    abstract fun remove(key: String)
    abstract fun clear()


    protected class Pixels<R : MyStyleDsl> {
        operator fun getValue(
            thisRef: R,
            property: KProperty<*>
        ): Px? {
            val s = thisRef[property.name.hyphenize()]
            if (s.isBlank()) return null
            return MarginSerializer.fromString(s) as Px
        }

        operator fun setValue(
            thisRef: R,
            property: KProperty<*>,
            value: Px?
        ) {
            if (value == null) thisRef.remove(property.name.hyphenize())
            else thisRef[property.name.hyphenize()] = value
        }
    }

    protected class Length<R : MyStyleDsl> {
        operator fun getValue(
            thisRef: R,
            property: KProperty<*>
        ): CssLength? {
            val s = thisRef[property.name.hyphenize()]
            if (s.isBlank()) return null

            if (s.isBlank()) return null
            return MarginSerializer.fromString(s) as CssLength
        }

        operator fun setValue(
            thisRef: R,
            property: KProperty<*>,
            value: CssLength?
        ) {
            if (value == null) thisRef.remove(property.name.hyphenize())
            else thisRef[property.name.hyphenize()] = value
        }
    }


    @OptIn(InternalSerializationApi::class)
    protected class EnumUnit<E : Enum<E>, R : MyStyleDsl>(val eCls: KClass<E>) {
        operator fun getValue(
            thisRef: R,
            property: KProperty<*>
        ): E? {
            val s = thisRef[property.name.hyphenize()].deHyphenize().takeIf { it.isNotBlank() } ?: return null
            return Json.decodeFromString(eCls.serializer(), "\"$s\"")
        }

        operator fun setValue(
            thisRef: R,
            property: KProperty<*>,
            value: E?
        ) {
            if (value == null) (thisRef).remove(property.name.hyphenize())
            else thisRef[property.name.hyphenize()] = value.name.hyphenize()
        }
    }


    protected class Custom<T, R : MyStyleDsl>(
        val converter: StringConverter<T & Any>
    ) {
        operator fun getValue(
            thisRef: R,
            property: KProperty<*>
        ): T = converter.fromString(thisRef[property.name.hyphenize()])

        operator fun setValue(
            thisRef: R,
            property: KProperty<*>,
            value: T
        ) {
            if (value == null) thisRef.remove(property.name.hyphenize())
            else thisRef[property.name.hyphenize()] = converter.toString(value)
        }
    }

    protected val raw = Custom<String, MyStyleDsl>(StringStringConverter)
}

abstract class CssStyleDSL : MyStyleDsl() {

    var color: ColorLike? by Custom(ColorLikeCssConverter)
    var textAlign by EnumUnit(TextAlign::class)
    var lineHeight by Length()


    var background: ColorLike? by Custom(ColorLikeCssConverter)
    var backgroundColor: ColorLike? by Custom(ColorLikeCssConverter)
    var borderColor: ColorLike? by Custom(ColorLikeCssConverter)

    var margin: Margin? by Custom(MarginSerializer)
    var verticalAlign: VerticalAlign?
        get() =
            this["vertical-align"].let { v ->
                VerticalAligns.entries.firstOrNull { it.name == v.deHyphenize() } ?: run {
                    if (v.isBlank()) null
                    else {
                        MarginSerializer.fromString(v) as VerticalAlign
                    }
                }
            }
        set(value) {
            if (value == null) remove("vertical-align")
            else this["vertical-align"] = value
        }

    var width by Length()
    var height by Length()
    var maxHeight by Length()
    var gap by Length()
    var zIndex: Int
        get() = this["z-index"].toInt()
        set(value) {
            this["z-index"] = value
        }
    var opacity: Number
        get() = this["opacity"].toDouble()
        set(value) {
            this["opacity"] = value
        }

    var float by EnumUnit(Float::class)
    var whiteSpace by EnumUnit(WhiteSpace::class)
    var display by EnumUnit(Display::class)
    var flexDirection by EnumUnit(FlexDirection::class)
    var position by EnumUnit(Position::class)
    var pointerEvents by EnumUnit(PointerEvents::class)
    var overflow by EnumUnit(Overflow::class)
    var justifyContent by EnumUnit(JustifyContent::class)
    var alignItems by EnumUnit(AlignItems::class)
    var boxSizing by EnumUnit(BoxSizing::class)
    var cursor by EnumUnit(Cursor::class)

    var visibility by EnumUnit(Visibility::class)

    var border by EnumUnit(BorderStyle::class)
    var outline by EnumUnit(Outline::class)
    var borderStyle by EnumUnit(BorderStyle::class)
    var borderWidth by EnumUnit(BorderWidth::class)
    var fontStyle by EnumUnit(FontStyle::class)
    var fontWeight by EnumUnit(FontWeight::class)
    var fontSize by Pixels()
    var marginLeft: Margin? by Custom(MarginSerializer)
    var marginTop: Margin? by Custom(MarginSerializer)
    var marginBottom: Margin? by Custom(MarginSerializer)
    var marginRight: Margin? by Custom(MarginSerializer)
    var paddingLeft: Margin? by Custom(MarginSerializer)
    var paddingTop: Margin? by Custom(MarginSerializer)
    var paddingBottom: Margin? by Custom(MarginSerializer)
    var paddingRight: Margin? by Custom(MarginSerializer)
    var padding: Margin? by Custom(MarginSerializer)
    var top by Length()
    var bottom by Length()
    var left by Length()
    var right by Length()

    var allSides: CssLength?
        get() = TODO()
        set(value) {
            top = value
            bottom = value
            left = value
            right = value
        }


    var transform: Transform
        get() = Transform.parse(this["transform"])
        set(value) {
            this["transform"] = value
        }

    var transformOrigin: PointIdea2<CssLength, CssLength>
        get() = TODO()
        set(value) {
            this["transform-origin"] = "${value.x.css} ${value.y.css}"
        }


    fun modifyTransform(op: Transform.() -> Unit) {
        transform = transform.apply(op)
    }

    fun resetTransform(op: Transform.() -> Unit) {
        transform =
            Transform().apply {
                op()
            }
    }

    fun transform(op: Transform.() -> Unit) = modifyTransform(op)

    var transition by raw
}
