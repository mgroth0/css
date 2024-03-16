package matt.css.read

import matt.css.rep.parsed.MyPsiCss
import matt.css.rep.parsed.builder.MyCssRuleBuilder
import matt.css.rep.parsed.builder.MyPsiCssBuilder


/*I literally cannot find any library for this. Whatever, how hard could this be?*/
fun parseCss(input: String): MyPsiCss {
    val builder = MyPsiCssBuilder()
    var state: State = Root
    val itr = input.iterator()
    while (itr.hasNext()) {
        val c = itr.nextChar()
        when (c) {
            '/' -> {
                check(itr.next() == '*')
                while (itr.hasNext()) {
                    if (itr.next() == '*') {
                        if (itr.next() == '/') {
                            break
                        }
                    }
                }
            }
            else ->
                when (state) {
                    Root -> {
                        if (!c.isWhitespace()) {
                            state = Selector(StringBuilder(c.toString()))
                        }
                    }

                    is Selector -> {
                        when (c) {
                            '{' -> {
                                val finalSelector = state.code.toString().trim()
                                val ruleBuilder = MyCssRuleBuilder(finalSelector)
                                state = Rule(ruleBuilder)
                            }
                            else -> state.code.append(c)
                        }
                    }

                    is Rule     -> {
                        if (c == '}') {
                            val rule = state.ruleBuilder.build()
                            builder.rule(rule)
                            state = Root
                        } else if (!c.isWhitespace()) {
                            val key =
                                buildString {
                                    append(c)
                                    do {
                                        val n = itr.nextChar()
                                        if (n == ':') break
                                        append(n)
                                    } while (true)
                                }.trim()
                            val value =
                                buildString {
                                    do {
                                        val n = itr.nextChar()
                                        if (n == ';') break
                                        append(n)
                                    } while (true)
                                }.trim()
                            state.ruleBuilder.property(key, value)
                        }
                    }
                }
        }
    }
    return builder.build()
}

private sealed interface State
private data object Root: State
private class Selector(val code: StringBuilder): State
private class Rule(val ruleBuilder: MyCssRuleBuilder): State
