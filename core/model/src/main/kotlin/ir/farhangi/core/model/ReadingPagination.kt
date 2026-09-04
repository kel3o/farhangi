package ir.farhangi.core.model

const val READING_PAGE_CHAR_LIMIT = 420

fun paginateReadingText(
    text: String,
    charLimit: Int = READING_PAGE_CHAR_LIMIT,
): List<String> {
    val normalized = text.trim()
    if (normalized.isEmpty()) return listOf("")
    val pages = mutableListOf<String>()
    val current = StringBuilder()
    fun flush() {
        if (current.isNotBlank()) {
            pages += current.toString().trim()
            current.clear()
        }
    }
    normalized.split(PARAGRAPH_BREAK).forEach { rawParagraph ->
        val paragraph = rawParagraph.trim()
        if (paragraph.isEmpty()) return@forEach
        if (current.isEmpty()) {
            appendChunk(paragraph, charLimit, current, pages)
            return@forEach
        }
        if (current.length + PARAGRAPH_SEPARATOR.length + paragraph.length <= charLimit) {
            current.append(PARAGRAPH_SEPARATOR).append(paragraph)
        } else {
            flush()
            appendChunk(paragraph, charLimit, current, pages)
        }
    }
    flush()
    return pages.ifEmpty { listOf(normalized) }
}

private fun appendChunk(
    text: String,
    charLimit: Int,
    current: StringBuilder,
    pages: MutableList<String>,
) {
    if (text.length <= charLimit) {
        current.append(text)
        return
    }
    splitLongText(text, charLimit).forEachIndexed { index, piece ->
        if (index == 0 && current.isEmpty()) {
            current.append(piece)
        } else {
            if (current.isNotBlank()) {
                pages += current.toString().trim()
                current.clear()
            }
            current.append(piece)
        }
    }
}

private fun splitLongText(text: String, charLimit: Int): List<String> {
    val sentences = text.split(SENTENCE_BREAK)
        .map { it.trim() }
        .filter { it.isNotEmpty() }
    if (sentences.isEmpty()) return chunkByLength(text, charLimit)
    val parts = mutableListOf<String>()
    val buffer = StringBuilder()
    sentences.forEach { sentence ->
        if (sentence.length > charLimit) {
            if (buffer.isNotBlank()) {
                parts += buffer.toString().trim()
                buffer.clear()
            }
            parts += chunkByLength(sentence, charLimit)
            return@forEach
        }
        if (buffer.isEmpty()) {
            buffer.append(sentence)
        } else if (buffer.length + 1 + sentence.length <= charLimit) {
            buffer.append(' ').append(sentence)
        } else {
            parts += buffer.toString().trim()
            buffer.clear()
            buffer.append(sentence)
        }
    }
    if (buffer.isNotBlank()) parts += buffer.toString().trim()
    return parts.ifEmpty { chunkByLength(text, charLimit) }
}

private fun chunkByLength(text: String, charLimit: Int): List<String> =
    text.chunked(charLimit).map { it.trim() }.filter { it.isNotEmpty() }

private val PARAGRAPH_BREAK = Regex("\\n{2,}")
private val SENTENCE_BREAK = Regex("(?<=[.!?؟。])\\s+")
private const val PARAGRAPH_SEPARATOR = "\n\n"
