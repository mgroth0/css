package matt.css.layout

import matt.css.CssStyleDSL
import matt.css.props.Position.absolute
import matt.css.units.percent
import matt.css.units.px


fun CssStyleDSL.fillParent() {
    position = absolute
    top = 0.px
    bottom = 0.px
    left = 0.px
    right = 0.px
}


fun CssStyleDSL.center() {
    position = absolute
    top = 50.percent
    left = 50.percent
    transform {
        translate(-50.percent, -50.percent)
    }
}