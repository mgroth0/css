package matt.css

import kotlinx.html.CommonAttributeGroupFacade
import kotlinx.html.style
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import matt.klib.hyphonizedToCamelCase
import matt.lang.inList
import matt.klib.str.lower
import matt.klib.str.toIntOrNullIfBlank
import kotlin.js.JsName
import kotlin.reflect.KClass
import kotlin.reflect.KProperty


val CommonAttributeGroupFacade.sty get() = HTMLDslStyleDSL(this)
fun CommonAttributeGroupFacade.sty(op: CssStyleDSL.()->Unit) = HTMLDslStyleDSL(this).op()

class HTMLDslStyleDSL(val tag: CommonAttributeGroupFacade): CssStyleDSL() {


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


@StyleDSLMarker
abstract class MyStyleDsl {

  abstract operator fun set(key: String, value: Any)
  abstract operator fun get(key: String): String
  abstract fun remove(key: String)
  abstract fun clear()


  protected class px<R: MyStyleDsl> {
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
  protected class length<R: MyStyleDsl> {
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
  protected class e<E: Enum<E>, R: MyStyleDsl>(val eCls: KClass<E>) {
	operator fun getValue(thisRef: R, property: KProperty<*>): E? {
	  val s = thisRef[property.name.hyphenize()].hyphonizedToCamelCase().takeIf { it.isNotBlank() } ?: return null
	  return Json.decodeFromString(eCls.serializer(), "\"$s\"")
	}

	operator fun setValue(thisRef: R, property: KProperty<*>, value: E?) {
	  if (value == null) (thisRef).remove(property.name.hyphenize())
	  else thisRef[property.name.hyphenize()] = value.name.hyphenize()
	}
  }


  protected class custom<T, R: MyStyleDsl>(
	val fromString: String.()->T,
	val toStringable: (T & Any).()->Any = { this }
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

abstract class CssStyleDSL: MyStyleDsl() {

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
	  VerticalAligns.values().firstOrNull { it.name == v.hyphonizedToCamelCase() }
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


  fun modifyTransform(op: Transform.()->Unit) {
	transform = transform.apply(op)
  }

  fun resetTransform(op: Transform.()->Unit) {
	transform = transform.apply {
	  funs.clear()
	  op()
	}
  }


}


class LinearGradient(@Suppress("UNUSED_PARAMETER") s: String): ColorLike {
  init {
	TODO()
  }

  val args = mutableListOf<String>()
}


@StyleDSLMarker
class Transform {
  companion object {

	private val transformFuns = mapOf<KClass<out TransformFun<*>>, (List<String>)->TransformFun<*>>(
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

  class Translate(override val args: Pair<Percent, Percent>): TransformFun<Pair<Percent, Percent>> {
	override fun parseArgs(args: List<String>): Pair<Percent, Percent> {
	  return args.map { it.toString().toPercent() }.let { it[0] to it[1] }
	}

	override fun toString(): String {
	  return this::class.simpleName!! + "(" + args.toList().joinToString(",") + ") "
	}
  }

  class Scale(override val args: Double): TransformFun<Double> {
	override fun parseArgs(args: List<String>): Double {
	  return args[0].toString().toDouble()
	}

	override fun toString(): String {
	  return this::class.simpleName!! + "(" + args.inList().joinToString(",") + ") "
	}
  }

  //  private val transforms

  fun translate(args: Pair<Percent, Percent>) {
	funs.add(Translate(args))
  }

  fun scale(args: Double) {
	funs.add(Scale(args))
  }

  //  var translate: Pair<Percent, Percent>
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


val Int.px get() = Px(this)
val Int.percent get() = Percent(this)


sealed interface Length


class Px(private val i: Int): Margin, Length, VerticalAlign { //NOSONAR
  operator fun unaryMinus() = Px(-i)
  override fun toString() = "${i}px"
  operator fun plus(other: Int) = Px(i + other)
  operator fun minus(other: Int) = Px(i - other)
  operator fun times(other: Int) = Px(i*other)
  operator fun div(other: Int) = Px(i/other)
  operator fun plus(other: Px) = Px(i + other.i)
  operator fun minus(other: Px) = Px(i - other.i)
  operator fun times(other: Px) = Px(i*other.i)
  operator fun div(other: Px) = Px(i/other.i)
}

class Percent(private val i: Int): Length, VerticalAlign { //NOSONAR
  operator fun unaryMinus() = Percent(-i)
  override fun toString() = "${i}%"
  operator fun plus(other: Int) = Percent(i + other)
  operator fun minus(other: Int) = Percent(i - other)
  operator fun times(other: Int) = Percent(i*other)
  operator fun div(other: Int) = Percent(i/other)
  operator fun plus(other: Percent) = Percent(i + other.i)
  operator fun minus(other: Percent) = Percent(i - other.i)
  operator fun times(other: Percent) = Percent(i*other.i)
  operator fun div(other: Percent) = Percent(i/other.i)
}


interface VerticalAlign

enum class VerticalAligns: VerticalAlign {
  initial,
  inherit,
  unset,
  baseline,
  sub,
  `super`,
  textTop,
  textBottom,
  middle,
  top,
  bottom;

  override fun toString(): String = name.hyphenize()
}


@Serializable
enum class AlignItems {
  stretch,
  center,
  flexStart,
  flexEnd,
  baseline,
  initial,
  inherit;

  override fun toString() = name.hyphenize()
}


@Serializable
enum class FontStyle {
  italic;

  override fun toString() = name.hyphenize()
}

@Serializable
enum class FlexDirection {
  row,
  rowReverse,
  column,
  columnReverse,
  initial,
  inherit;

  override fun toString() = name.hyphenize()
}

@Serializable
enum class BoxSizing {
  contentBox, borderBox, initial, inherit;

  override fun toString() = name.hyphenize()
}

@Serializable
enum class FontWeight {
  bold;

  override fun toString() = name.hyphenize()
}

@Serializable
enum class BorderStyle {
  solid;

  override fun toString() = name.hyphenize()
}

@Serializable
enum class BorderWidth {
  thin;

  override fun toString() = name.hyphenize()
}

@Serializable
enum class Overflow {
  visible,
  hidden,
  scroll,
  auto;

  override fun toString() = name
}

@Serializable
enum class Position {
  relative,
  absolute;

  override fun toString() = name
}

//enum class matt.klib.css.Display(val s: String? = null) {
//  inline,
//  block,
//  contents,
//  flex,
//  grid,
//  inlineBlock("inline-block"),
//  inlineFlex("inline-flex"),
//  inlineGrid("inline-grid"),
//  inlineTable("inline-table"),
//  listItem("list-item"),
//  runIn("run-in"),
//  table,
//  tableCaption("table-caption"),
//  tableColumnGroup("table-column-group"),
//  tableHeaderGroup("table-header-group"),
//  tableFooterGroup("table-footer-group"),
//  table-r
//
//  override fun toString() = s ?: name
//}

//val a = 1.apply {
//
//}


@Serializable
enum class JustifyContent {
  initial, inherit, unset,

  center,
  start,
  end,
  flexStart,
  flexEnd,
  left,
  right,
  baseline,
  firstBaseline,
  lastBaseline,
  spaceBetween,
  spaceAround,
  spaceEvenly,
  stretch,
  safeCenter,
  unsafeCenter;

  override fun toString() = name.hyphenize()
}

@Serializable
enum class TextAlign() {
  center;

  override fun toString() = name
}

sealed interface Margin

object auto: Margin {
  override fun toString() = this::class.simpleName!!
}

interface ColorLike

@Serializable
enum class Color: ColorLike {

  black,


  white, blue, red, orange, green, aqua, grey, purple, violet, yellow;

  override fun toString() = name
}


@Serializable
enum class Display {
  initial, inherit, unset,

  block, `inline`, runIn,

  flow, flowRoot, table, flex, grid, subgrid,

  listItem,

  tableRowGroup, tableHeaderGroup, tableFooterGroup, tableRow, tableCell, tableColumnGroup, tableColumn, tableCaption,

  contents, none,

  inlineBlock, inlineListItem, inlineTable, inlineFlex, inlineGrid;

  override fun toString(): String = name.hyphenize()
}

@Serializable
enum class Cursor {
  initial, inherit, unset,

  auto, default, none, // General
  contextMenu, help, pointer, progress, wait, // Links & status
  cell, crosshair, text, verticalText, // Selection
  alias, copy, move, noDrop, notAllowed, grab, grabbing, // Drag and drop
  colResize, rowResize, allScroll, // Resize & scrolling
  eResize, nResize, neResize, nwResize, sResize, seResize, swResize, wResize, // Directed resize
  ewResize, nsResize, neswResize, nwseResize, // Bidirectional resize
  zoomIn, zoomOut; // Zoom

  override fun toString() = name.hyphenize()
}

private val CAPITAL_LETTER = Regex("[A-Z]")

fun String.hyphenize(): String =
  replace(CAPITAL_LETTER) {
	"-${it.value.lowercase()}"
  }