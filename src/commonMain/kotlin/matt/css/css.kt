package matt.css

import kotlinx.css.PointerEvents
import kotlinx.css.Visibility
import kotlinx.html.CommonAttributeGroupFacade
import kotlinx.html.style
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.internal.FormatLanguage
import kotlinx.serialization.serializer
import matt.color.ColorLike
import matt.css.props.AlignItems
import matt.css.props.BorderStyle
import matt.css.props.BorderWidth
import matt.css.props.BoxSizing
import matt.css.props.ColorLikeCssConverter
import matt.css.props.Cursor
import matt.css.props.Display
import matt.css.props.FlexDirection
import matt.css.props.Float
import matt.css.props.FontStyle
import matt.css.props.FontWeight
import matt.css.props.JustifyContent
import matt.css.props.Outline
import matt.css.props.Overflow
import matt.css.props.Position
import matt.css.props.TextAlign
import matt.css.props.VerticalAlign
import matt.css.props.VerticalAligns
import matt.css.props.WhiteSpace
import matt.css.ser.MarginCssConverter
import matt.css.transform.Transform
import matt.css.units.CssLength
import matt.css.units.Margin
import matt.css.units.Px
import matt.lang.idea.PointIdea2
import matt.lang.mime.BinaryRepresentableData
import matt.lang.mime.CachingTextData
import matt.lang.mime.TextMimeData
import matt.lang.model.file.types.MimeType
import matt.lang.model.value.MyValueClass
import matt.lang.model.value.MyValueClassSerializer
import matt.model.op.convert.StringStringConverter
import matt.prim.converters.StringConverter
import matt.prim.str.cases.DromedaryCase
import matt.prim.str.cases.LowerKebabCase
import matt.prim.str.cases.convert
import kotlin.js.JsName
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

private val CSS_MIME_TYPE = MimeType("text", "css")

@Serializable(with = RuledCss.Serializer::class)
class RuledCss(@FormatLanguage("CSS", "", "") val code: String) : MyValueClass<String>(code),
    TextMimeData, BinaryRepresentableData by CachingTextData(code) {
    companion object Serializer : MyValueClassSerializer<String, RuledCss>(serializer<String>()) {
        override fun construct(value: String) = RuledCss(value)
    }

    override val mimeType get() = CSS_MIME_TYPE
    override val asText get() = code
}

@Serializable(with = InlineCss.Serializer::class)
class InlineCss(@FormatLanguage("CSS", "", "") val code: String) : MyValueClass<String>(code),
    TextMimeData, BinaryRepresentableData by CachingTextData(code) {
        companion object Serializer : MyValueClassSerializer<String, InlineCss>(serializer<String>()) {
        override fun construct(value: String) = InlineCss(value)
    }

    override val mimeType get() = CSS_MIME_TYPE
    override val asText: String get() = code
}

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
            tag.style += " ${key}: $value;"
        }
    }

    override fun get(key: String): String = TODO()

    override fun remove(key: String) = TODO()

    override fun clear() = TODO()
}


@DslMarker
annotation class StyleDSLMarker

internal fun String.hyphenize() = convert(DromedaryCase, LowerKebabCase)
internal fun String.deHyphenize() = convert(LowerKebabCase, DromedaryCase)

@StyleDSLMarker
abstract class MyStyleDsl {

    abstract operator fun set(
        key: String,
        value: Any
    )

    abstract operator fun get(key: String): String
    abstract fun remove(key: String)
    abstract fun clear()


    protected class px<R : MyStyleDsl> {
        operator fun getValue(
            thisRef: R,
            property: KProperty<*>
        ): Px? {
            val s = thisRef[property.name.hyphenize()]
            if (s.isBlank()) return null
            return MarginCssConverter.fromString(s) as Px
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

    @JsName("lengthProp")
    protected class length<R : MyStyleDsl> {
        operator fun getValue(
            thisRef: R,
            property: KProperty<*>
        ): CssLength? {
            val s = thisRef[property.name.hyphenize()]
            if (s.isBlank()) return null

            if (s.isBlank()) return null
            return MarginCssConverter.fromString(s) as CssLength

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
    protected class e<E : Enum<E>, R : MyStyleDsl>(val eCls: KClass<E>) {
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


    protected class custom<T, R : MyStyleDsl>(
        val converter: StringConverter<T & Any>,
    ) {
        operator fun getValue(
            thisRef: R,
            property: KProperty<*>
        ): T {
            return converter.fromString(thisRef[property.name.hyphenize()])
        }

        operator fun setValue(
            thisRef: R,
            property: KProperty<*>,
            value: T
        ) {
            if (value == null) thisRef.remove(property.name.hyphenize())
            else thisRef[property.name.hyphenize()] = converter.toString(value)
        }
    }

    protected val raw = custom<String, MyStyleDsl>(StringStringConverter)
//    protected object raw {
//        operator fun getValue(
//            thisRef: MyStyleDsl,
//            property: KProperty<*>
//        ): String {
//            return thisRef[property.name.hyphenize()]
//        }
//
//        operator fun setValue(
//            thisRef: R,
//            property: KProperty<*>,
//            value: T
//        ) {
//            if (value == null) thisRef.remove(property.name.hyphenize())
//            else thisRef[property.name.hyphenize()] = converter.toString(value)
//        }
//    }
}

abstract class CssStyleDSL : MyStyleDsl() {

    var color: ColorLike? by custom(ColorLikeCssConverter)
    var textAlign by e(TextAlign::class)
    var lineHeight by length()


    var background: ColorLike? by custom(ColorLikeCssConverter)
    var backgroundColor: ColorLike? by custom(ColorLikeCssConverter)
    var borderColor: ColorLike? by custom(ColorLikeCssConverter)

    var margin: Margin? by custom(MarginCssConverter)
    var verticalAlign: VerticalAlign?
        get() = this["vertical-align"].let { v ->
            VerticalAligns.entries.firstOrNull { it.name == v.deHyphenize() } ?: run {
                if (v.isBlank()) null
                else {
                    MarginCssConverter.fromString(v) as VerticalAlign
                }
//                    if ("px" in v) v.toPxOrNullIfBlank() else v.toPercentOrNullIfBlank()
            }
        }
        set(value) {
            if (value == null) remove("vertical-align")
            else this["vertical-align"] = value
        }

    var width by length()
    var height by length()
    var maxHeight by length()
    var gap by length()
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

    var float by e(Float::class)
    var whiteSpace by e(WhiteSpace::class)
    var display by e(Display::class)
    var flexDirection by e(FlexDirection::class)
    var position by e(Position::class)
    var pointerEvents by e(PointerEvents::class)
    var overflow by e(Overflow::class)
    var justifyContent by e(JustifyContent::class)
    var alignItems by e(AlignItems::class)
    var boxSizing by e(BoxSizing::class)
    var cursor by e(Cursor::class)

    var visibility by e(Visibility::class)

    var border by e(BorderStyle::class)
    var outline by e(Outline::class)
    var borderStyle by e(BorderStyle::class)
    var borderWidth by e(BorderWidth::class)
    var fontStyle by e(FontStyle::class)
    var fontWeight by e(FontWeight::class)
    var fontSize by px()
    var marginLeft: Margin? by custom(MarginCssConverter)
    var marginTop: Margin? by custom(MarginCssConverter)
    var marginBottom: Margin? by custom(MarginCssConverter)
    var marginRight: Margin? by custom(MarginCssConverter)
    var paddingLeft: Margin? by custom(MarginCssConverter)
    var paddingTop: Margin? by custom(MarginCssConverter)
    var paddingBottom: Margin? by custom(MarginCssConverter)
    var paddingRight: Margin? by custom(MarginCssConverter)
    var padding: Margin? by custom(MarginCssConverter)
    var top by length()
    var bottom by length()
    var left by length()
    var right by length()

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
        transform = Transform().apply {
            op()
        }
    }

    fun transform(op: Transform.() -> Unit) = modifyTransform(op)

    var transition by raw

}






