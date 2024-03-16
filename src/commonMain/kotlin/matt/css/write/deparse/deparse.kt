package matt.css.write.deparse

import matt.css.rep.parsed.MyPsiCss


fun MyPsiCss.deParse(
    pretty: Boolean = false
): String =
    buildString {
        rules.forEach { rule ->
            append(rule.selector.code)
            append(" {\n")
            rule.properties.forEach {
                if (pretty) append('\t')
                appendLine("${it.key}: ${it.value};")
            }
            appendLine("}")
            if (pretty) appendLine()
        }
    }
