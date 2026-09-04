package ir.farhangi.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReadingPaginationTest {

    @Test
    fun emptyText_returnsSingleBlankPage() {
        assertEquals(listOf(""), paginateReadingText("   "))
    }

    @Test
    fun shortText_staysOnOnePage() {
        val pages = paginateReadingText("گلستان سعدی را آرام بخوانید.")
        assertEquals(1, pages.size)
        assertEquals("گلستان سعدی را آرام بخوانید.", pages.first())
    }

    @Test
    fun longParagraphs_splitIntoMultiplePages() {
        val paragraph = "این یک بند آزمایشی برای صفحه‌بندی خلاصه کتاب است. ".repeat(12)
        val pages = paginateReadingText(paragraph, charLimit = 80)
        assertTrue(pages.size > 1)
        pages.forEach { page ->
            assertTrue(page.length <= 80 || page.split(" ").size == 1)
        }
    }

    @Test
    fun paragraphsStayTogetherWhenTheyFit() {
        val text = "بند اول کوتاه است.\n\nبند دوم هم کوتاه است."
        val pages = paginateReadingText(text, charLimit = 200)
        assertEquals(1, pages.size)
        assertTrue(pages.first().contains("بند اول"))
        assertTrue(pages.first().contains("بند دوم"))
    }
}
