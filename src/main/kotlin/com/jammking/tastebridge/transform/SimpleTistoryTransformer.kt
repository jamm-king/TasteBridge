package com.jammking.tastebridge.transform

import com.jammking.tastebridge.model.CrawledPage
import com.jammking.tastebridge.model.Review
import org.jsoup.Jsoup
import org.springframework.stereotype.Component

@Component
class SimpleTistoryTransformer: PageToReviewTransformer {
    override fun toReview(page: CrawledPage): Review? {
        val text = when {
            !page.text.isNullOrBlank() -> page.text.trim()
            !page.html.isNullOrBlank() -> Jsoup.parse(page.html).text().trim()
            else -> null
        } ?: return null

        if(text.length < 50) return null

        return Review(
            url = page.url,
            text = text,
            metadata = mapOf(
                "title" to (page.title ?: ""),
                "origin" to "webprobe",
                "sourceEngine" to "TISTORY"
            )
        )
    }
}