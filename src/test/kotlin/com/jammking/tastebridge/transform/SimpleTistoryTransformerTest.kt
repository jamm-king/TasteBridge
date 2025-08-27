package com.jammking.tastebridge.transform

import com.jammking.tastebridge.model.CrawledPage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull

class SimpleTistoryTransformerTest {

    private val transformer = SimpleTistoryTransformer()

    @Test
    fun `skip when content is too short`() {
        val page = CrawledPage(url = "https://tistory/1", text = "too short")

        assertNull(transformer.toReview(page))
    }

    @Test
    fun `use html fallback when text is null`() {
        val html = "<article><p>this is a fairly long text that exceeds fifty characters to pass the threshhold for review.</p></article>"
        val page = CrawledPage(url = "https://tistory/2", title = "맛집", html = html, text = null)
        val review = transformer.toReview(page)

        assertNotNull(review)
        assertEquals("https://tistory/2", review.url)
        assertTrue(review.text.length >= 50)
        assertEquals("맛집", review.metadata["title"])
        assertEquals("webprobe", review.metadata["origin"])
        assertEquals("TISTORY", review.metadata["sourceEngine"])
    }

    @Test
    fun `build review from adequate plan text`() {
        val page = CrawledPage(url = "https://tistory/3", title = "후기", text = "a".repeat(80))
        val review = transformer.toReview(page)
        assertNotNull(review)
        assertEquals("후기", review.metadata["title"])
    }
}