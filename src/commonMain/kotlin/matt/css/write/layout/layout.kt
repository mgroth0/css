package matt.css.write.layout

import matt.css.rep.props.Position.absolute
import matt.css.rep.units.percent
import matt.css.rep.units.px
import matt.css.write.CssStyleDSL


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
