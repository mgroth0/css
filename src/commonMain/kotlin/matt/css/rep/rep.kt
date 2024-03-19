@file:OptIn(InternalSerializationApi::class)

package matt.css.rep

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.internal.FormatLanguage
import kotlinx.serialization.serializer
import matt.css.rep.RuledCss.Serializer
import matt.lang.mime.BinaryRepresentableData
import matt.lang.mime.CachingTextData
import matt.lang.mime.MimeType
import matt.lang.mime.TextMimeData
import matt.lang.model.value.MyValueClass
import matt.model.data.value.MyValueClassSerializer

private val CSS_MIME_TYPE = MimeType("text", "css")

@Serializable(with = RuledCss.Serializer::class)
class RuledCss(
    @FormatLanguage("CSS", "", "") val code: String
) :
    MyValueClass<String>(code),
        TextMimeData,
        BinaryRepresentableData by CachingTextData(code) {
    companion object Serializer : MyValueClassSerializer<String, RuledCss>(serializer<String>()) {
        override fun construct(value: String) = RuledCss(value)
    }

    override val mimeType get() = CSS_MIME_TYPE
    override val asText get() = code
}

@Serializable(with = InlineCss.Serializer::class)
class InlineCss(
    @FormatLanguage("CSS", "", "") val code: String
) :
    MyValueClass<String>(code),
        TextMimeData,
        BinaryRepresentableData by CachingTextData(code) {
    companion object Serializer : MyValueClassSerializer<String, InlineCss>(serializer<String>()) {
        override fun construct(value: String) = InlineCss(value)
    }

    override val mimeType get() = CSS_MIME_TYPE
    override val asText: String get() = code
}
