package matt.css.rep.props

import matt.color.common.ColorLike
import matt.css.hyphenize
import matt.css.rep.gradient.LinearGradient
import matt.prim.converters.StringConverter

enum class Cursor {
    initial, inherit, unset,

    auto, default, none, /* General */
    contextMenu, help, pointer, progress, wait, /* Links & status */
    cell, crosshair, text, verticalText, /* Selection */
    alias, copy, move, noDrop, notAllowed, grab, grabbing, /* Drag and drop */
    colResize, rowResize, allScroll, /* Resize & scrolling */
    eResize, nResize, neResize, nwResize, sResize, seResize, swResize, wResize, /* Directed resize */
    ewResize, nsResize, neswResize, nwseResize, /* Bidirectional resize */
    zoomIn, zoomOut; /* Zoom */

    override fun toString() = name.hyphenize()
}


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

enum class WhiteSpace {
    preWrap;

    override fun toString(): String = name.hyphenize()
}

enum class Float {
    right;

    override fun toString(): String = name.hyphenize()
}





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

enum class TextAlign {
    center, left;

    override fun toString() = name
}



object ColorLikeCssConverter: StringConverter<ColorLike> {
    override fun toString(t: ColorLike): String = t.css

    override fun fromString(s: String): ColorLike = if ("linear-gradient" in s) LinearGradient(s) else Color.valueOf(s)
}

enum class Color : ColorLike {

    black,


    white, blue, red, orange, green, aqua, grey, purple, violet, yellow, transparent;

    override fun toString() = name

    override val css = name
}


interface VerticalAlign

enum class VerticalAligns : VerticalAlign {
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


enum class FontStyle {
    italic;

    override fun toString() = name.hyphenize()
}

enum class FlexDirection {
    row,
    rowReverse,
    column,
    columnReverse,
    initial,
    inherit;

    override fun toString() = name.hyphenize()
}

enum class BoxSizing {
    contentBox, borderBox, initial, inherit;

    override fun toString() = name.hyphenize()
}

enum class FontWeight {
    bold;

    override fun toString() = name.hyphenize()
}

enum class BorderStyle {
    solid, none;

    override fun toString() = name.hyphenize()
}
enum class Outline {
    solid, none;

    override fun toString() = name.hyphenize()
}

enum class BorderWidth {
    thin;

    override fun toString() = name.hyphenize()
}

enum class Overflow {
    visible,
    hidden,
    scroll,
    auto;

    override fun toString() = name
}

enum class Position {
    relative,
    absolute;

    override fun toString() = name
}
