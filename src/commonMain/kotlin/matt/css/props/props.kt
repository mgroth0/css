package matt.css.props

import kotlinx.serialization.Serializable
import matt.prim.str.cases.hyphenize

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
enum class WhiteSpace {
    preWrap;

    override fun toString(): String = name.hyphenize()
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



interface ColorLike

@Serializable
enum class Color: ColorLike {

    black,


    white, blue, red, orange, green, aqua, grey, purple, violet, yellow, transparent;

    override fun toString() = name
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