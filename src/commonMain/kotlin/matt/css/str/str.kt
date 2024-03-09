package matt.css.str

import matt.css.CssStyleDSL
import matt.css.InlineCss
import matt.lang.function.Dsl

fun inlineCss(op: Dsl<CssStyleDSL>) = InlineCss(InlineCssStringBuilderDsl().apply(op).build())

class InlineCssStringBuilderDsl internal constructor() : CssStyleDSL() {

    private val map =
        mutableMapOf<String, String>() /*Does preserve order, which can matter. See: https://stackoverflow.com/questions/16794379/position-of-css-properties-order-matter-or-not*/

    override operator fun set(
        key: String,
        value: Any
    ) {
        map[key] = value.toString()
    }

    override fun get(key: String): String = map[key]!!

    override fun remove(key: String) {
        map.remove(key)
    }

    override fun clear() {
        map.clear()
    }

    internal fun build() = map.entries.joinToString(separator = "; ") { "${it.key}: ${it.value}" }
}
