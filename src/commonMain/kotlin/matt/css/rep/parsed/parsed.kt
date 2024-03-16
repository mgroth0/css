package matt.css.rep.parsed

import matt.css.rep.parsed.builder.MutableCssProperty
import matt.css.rep.parsed.builder.MyCssRuleBuilder
import matt.css.rep.parsed.builder.MyPsiCssBuilder
import matt.css.write.deparse.deParse






class MyPsiCss(
    val rules: List<CssRule>
) {
    override fun toString(): String = deParse()
    fun toMutable() = MyPsiCssBuilder(rules)
}

class CssRule(
    val selector: CssSelector,
    val properties: List<CssProperty>

) {
    fun toMutable() = MyCssRuleBuilder(selector.code, properties)
}

class CssSelector(val code: String)

class CssProperty(
    val key: String,
    val value: String
) {
    fun toMutable() = MutableCssProperty(key, value)
}
