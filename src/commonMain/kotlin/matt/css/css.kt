package matt.css

import kotlinx.html.CommonAttributeGroupFacade
import kotlinx.html.style
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import matt.css.props.AlignItems
import matt.css.props.BorderStyle
import matt.css.props.BorderWidth
import matt.css.props.BoxSizing
import matt.css.props.Color
import matt.css.props.ColorLike
import matt.css.props.Cursor
import matt.css.props.Display
import matt.css.props.FlexDirection
import matt.css.props.FontStyle
import matt.css.props.FontWeight
import matt.css.props.JustifyContent
import matt.css.props.Overflow
import matt.css.props.Position
import matt.css.props.TextAlign
import matt.css.props.VerticalAlign
import matt.css.props.VerticalAligns
import matt.css.props.WhiteSpace
import matt.css.units.Length
import matt.css.units.Margin
import matt.css.units.Px
import matt.css.units.auto
import matt.css.units.toPercent
import matt.css.units.toPercentOrNullIfBlank
import matt.css.units.toPx
import matt.css.units.toPxOrNullIfBlank
import matt.lang.inList
import matt.model.code.idea.LinearGradientIdea
import matt.prim.str.cases.DromedaryCase
import matt.prim.str.cases.LowerKebabCase
import matt.prim.str.cases.convert
import matt.prim.str.lower
import kotlin.js.JsName
import kotlin.reflect.KClass
import kotlin.reflect.KProperty


val CommonAttributeGroupFacade.sty get() = HTMLDslStyleDSL(this)
fun CommonAttributeGroupFacade.sty(op: CssStyleDSL.() -> Unit) = HTMLDslStyleDSL(this).op()

class HTMLDslStyleDSL(val tag: CommonAttributeGroupFacade) : CssStyleDSL() {


    companion object {
        private const val STYLE_KEY = "style"
    }

    override operator fun set(key: String, value: Any) {
        if (STYLE_KEY !in tag.attributes) {
            tag.style = "$key: $value;"
        } else {
            tag.style += " ${key}: $value;"
        }
    }

    override fun get(key: String): String {
        TODO("Not yet implemented")
    }

    override fun remove(key: String) {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }
}


@DslMarker
annotation class StyleDSLMarker

internal fun String.hyphenize() = convert(DromedaryCase, LowerKebabCase)
internal fun String.deHyphenize() = convert(LowerKebabCase, DromedaryCase)

@StyleDSLMarker
abstract class MyStyleDsl {

    abstract operator fun set(key: String, value: Any)
    abstract operator fun get(key: String): String
    abstract fun remove(key: String)
    abstract fun clear()


    protected class px<R : MyStyleDsl> {
        operator fun getValue(thisRef: R, property: KProperty<*>): Px? {
            val s = thisRef[property.name.hyphenize()]
            return s.toPxOrNullIfBlank()
        }

        operator fun setValue(thisRef: R, property: KProperty<*>, value: Px?) {
            if (value == null) thisRef.remove(property.name.hyphenize())
            else thisRef[property.name.hyphenize()] = value
        }
    }

    @JsName("lengthProp")
    protected class length<R : MyStyleDsl> {
        operator fun getValue(thisRef: R, property: KProperty<*>): Length? {
            val s = thisRef[property.name.hyphenize()]
            if (s.isBlank()) return null
            else if ("px" in s) return s.toPx()
            return s.toPercent()
        }

        operator fun setValue(thisRef: R, property: KProperty<*>, value: Length?) {
            if (value == null) thisRef.remove(property.name.hyphenize())
            else thisRef[property.name.hyphenize()] = value
        }
    }

    @OptIn(InternalSerializationApi::class)
    protected class e<E : Enum<E>, R : MyStyleDsl>(val eCls: KClass<E>) {
        operator fun getValue(thisRef: R, property: KProperty<*>): E? {
            val s = thisRef[property.name.hyphenize()].deHyphenize().takeIf { it.isNotBlank() } ?: return null
            return Json.decodeFromString(eCls.serializer(), "\"$s\"")
        }

        operator fun setValue(thisRef: R, property: KProperty<*>, value: E?) {
            if (value == null) (thisRef).remove(property.name.hyphenize())
            else thisRef[property.name.hyphenize()] = value.name.hyphenize()
        }
    }


    protected class custom<T, R : MyStyleDsl>(
        val fromString: String.() -> T,
        val toStringable: (T & Any).() -> Any = { this }
    ) {
        operator fun getValue(thisRef: R, property: KProperty<*>): T {
            return fromString(thisRef[property.name.hyphenize()])
        }

        operator fun setValue(thisRef: R, property: KProperty<*>, value: T) {
            if (value == null) thisRef.remove(property.name.hyphenize())
            else thisRef[property.name.hyphenize()] = toStringable(value)
        }
    }
}

abstract class CssStyleDSL : MyStyleDsl() {

    var color: ColorLike? by custom({
        if ("linear-gradient" in this) LinearGradient(this) else Color.valueOf(this)
    })
    var textAlign by e(TextAlign::class)
    var lineHeight by length()


    var background: ColorLike? by custom({
        if ("linear-gradient" in this) LinearGradient(this) else Color.valueOf(this)
    })
    var borderColor: ColorLike? by custom({
        if ("linear-gradient" in this) LinearGradient(this) else Color.valueOf(this)
    })
    var margin: Margin? by custom({
        if (this == auto::class.simpleName!!) auto else toPxOrNullIfBlank()
    })
    var verticalAlign: VerticalAlign?
        get() = this["vertical-align"].let { v ->
            VerticalAligns.values().firstOrNull { it.name == v.deHyphenize() }
                ?: if ("px" in v) v.toPxOrNullIfBlank() else v.toPercentOrNullIfBlank()
        }
        set(value) {
            if (value == null) remove("vertical-align")
            else this["vertical-align"] = value
        }

    var width by length()
    var height by length()
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


    var whiteSpace by e(WhiteSpace::class)
    var display by e(Display::class)
    var flexDirection by e(FlexDirection::class)
    var position by e(Position::class)
    var overflow by e(Overflow::class)
    var justifyContent by e(JustifyContent::class)
    var alignItems by e(AlignItems::class)
    var boxSizing by e(BoxSizing::class)
    var cursor by e(Cursor::class)

    var borderStyle by e(BorderStyle::class)
    var borderWidth by e(BorderWidth::class)
    var fontStyle by e(FontStyle::class)
    var fontWeight by e(FontWeight::class)
    var fontSize by px()
    var marginLeft: Margin? by custom({
        if (this == auto::class.simpleName!!) auto else toPxOrNullIfBlank()
    })
    var marginTop: Margin? by custom({
        if (this == auto::class.simpleName!!) auto else toPxOrNullIfBlank()
    })
    var marginBottom: Margin? by custom({
        if (this == auto::class.simpleName!!) auto else toPxOrNullIfBlank()
    })
    var marginRight: Margin? by custom({
        if (this == auto::class.simpleName!!) auto else toPxOrNullIfBlank()
    })
    var paddingLeft: Margin? by custom({
        if (this == auto::class.simpleName!!) auto else toPxOrNullIfBlank()
    })
    var paddingTop: Margin? by custom({
        if (this == auto::class.simpleName!!) auto else toPxOrNullIfBlank()
    })
    var paddingBottom: Margin? by custom({
        if (this == auto::class.simpleName!!) auto else toPxOrNullIfBlank()
    })
    var paddingRight: Margin? by custom({
        if (this == auto::class.simpleName!!) auto else toPxOrNullIfBlank()
    })
    var padding: Margin? by custom({
        if (this == auto::class.simpleName!!) auto else toPxOrNullIfBlank()
    })
    var top by length()
    var bottom by length()
    var left by length()
    var right by length()


    var transform: Transform
        get() = Transform.parse(this["transform"])
        set(value) {
            this["transform"] = value
        }


    fun modifyTransform(op: Transform.() -> Unit) {
        transform = transform.apply(op)
    }

    fun resetTransform(op: Transform.() -> Unit) {
        transform = transform.apply {
            funs.clear()
            op()
        }
    }


}


class LinearGradient(@Suppress("UNUSED_PARAMETER") s: String) : ColorLike, LinearGradientIdea {
    init {
        TODO()
    }

    val args = mutableListOf<String>()
}


@StyleDSLMarker
class Transform {
    companion object {

        private val transformFuns = mapOf<KClass<out TransformFun<*>>, (List<String>) -> TransformFun<*>>(
            Translate::class to { Translate(it.map { it.toString().toPercent() }.let { it[0] to it[1] }) },
            Scale::class to { Scale(it[0].toString().toDouble()) }
        )

        fun parse(s: String): Transform {
            val t = Transform()
            if (s.isBlank()) return t

            var funName: String? = null
            s.split("(").forEach {
                if (funName == null) {
                    funName = it.trim()
                } else {
                    val args = it.substringBefore(")").split(",")
                    t.funs.add(
                        transformFuns.entries.first { it.key.simpleName!!.lower() == funName!! }.value(args)
                    )
                    //		  t.map[funName!!] = args
                    funName = it.substringAfter(")").takeIf { it.isNotBlank() }?.trim()
                }
            }
            return t
        }
    }

    /*ORDER MATTERS, DUPLICATES POSSIBLE*/
    /*order matters and functions can be used multiple times!*/
    /*ORDER MATTERS, DUPLICATES POSSIBLE*/
    val funs = mutableListOf<TransformFun<*>>()
    /*ORDER MATTERS, DUPLICATES POSSIBLE*/

    sealed interface TransformFun<A> {
        val args: A
        fun parseArgs(args: List<String>): A
    }

    class Translate(override val args: Pair<Length, Length>) : TransformFun<Pair<Length, Length>> {
        override fun parseArgs(args: List<String>): Pair<Length, Length> {
            return args.map { it.toString().toPercent() }.let { it[0] to it[1] }
        }

        override fun toString(): String {
            return this::class.simpleName!! + "(" + args.toList().joinToString(",") + ") "
        }
    }

    class Scale(override val args: Double) : TransformFun<Double> {
        override fun parseArgs(args: List<String>): Double {
            return args[0].toString().toDouble()
        }

        override fun toString(): String {
            return this::class.simpleName!! + "(" + args.inList().joinToString(",") + ") "
        }
    }

    //  private val transforms

    fun translate(args: Pair<Length, Length>) {
        funs.add(Translate(args))
    }

    fun scale(args: Double) {
        funs.add(Scale(args))
    }

    //  var translate: Pair<matt.model.percent.Percent, matt.model.percent.Percent>
    //	get() = map["translate"]!!.map { it.toString().toPercent() }.let { it[0] to it[1] }
    //	set(value) {
    //	  map["translate"] = value.toList()
    //	}
    //  var scale: Double
    //	get() = map["scale"]!![0].toString().toDouble()
    //	set(value) {
    //	  map["scale"] = value.inList()
    //	}

    override fun toString(): String {
        var s = ""
        funs.forEach {
            s += it.toString()
            //	  s += it::class.simpleName!!.lower() + "("
            //	  s += it.args.joinToString(",")
            //	  s += ") "
        }
        return s
    }
}






