package matt.css.gradient

import matt.color.ColorLike
import matt.model.code.idea.LinearGradientIdea

class LinearGradient(@Suppress("UNUSED_PARAMETER") s: String) : ColorLike, LinearGradientIdea {
    init {
        TODO()
    }

    val args = mutableListOf<String>()

    override val css get() = TODO()
}
