package matt.css.dark

import kotlinx.css.Color.Companion.black
import kotlinx.css.Color.Companion.white
import kotlinx.css.CssBuilder
import kotlinx.css.backgroundColor
import kotlinx.css.body
import kotlinx.css.color
import matt.lang.function.Dsl


val DARK: Dsl<CssBuilder> = {
    body {
        backgroundColor = black
        color = white
    }
}