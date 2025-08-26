package com.jammking.tastebridge.transform

import com.jammking.tastebridge.model.CrawledPage
import com.jammking.tastebridge.model.Review

interface PageToReviewTransformer {
    fun toReview(page: CrawledPage): Review?
}