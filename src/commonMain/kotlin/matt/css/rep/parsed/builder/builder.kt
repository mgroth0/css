package matt.css.rep.parsed.builder

import matt.css.rep.parsed.CssProperty
import matt.css.rep.parsed.CssRule
import matt.css.rep.parsed.CssSelector
import matt.css.rep.parsed.MyPsiCss



class MyPsiCssBuilder(
    rules: Iterable<CssRule> = emptyList()
) {
    private val rules: MutableList<MyCssRuleBuilder> =
        rules.map {
            it.toMutable()
        }.toMutableList()

    fun rule(rule: CssRule) {
        rules.add(rule.toMutable())
    }

    fun rules() = rules.toList()

    fun build() = MyPsiCss(rules.map { it.build() })
}


class MyCssRuleBuilder(
    private val selector: String,
    properties: Iterable<CssProperty> = emptyList()
) {
    private val properties: MutableList<MutableCssProperty> = properties.map { it.toMutable() }.toMutableList()
    fun property(key: String, value: String) {
        properties.add(CssProperty(key, value).toMutable())
    }
    fun properties() = properties.toList()
    fun build() = CssRule(CssSelector(selector), properties.map { it.build() })
}


class MutableCssProperty(
    val key: String,
    var value: String
) {
    fun build() = CssProperty(key, value)
}
