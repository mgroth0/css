package matt.css.rep.gradient

import matt.color.common.ColorLike
import matt.model.code.idea.LinearGradientIdea

class LinearGradient(
    @Suppress("UNUSED_PARAMETER") s: String
) : ColorLike, LinearGradientIdea {
    init {
        TODO()
    }

    val args = mutableListOf<String>()

    override val css get() = TODO()
}
