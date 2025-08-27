package com.jammking.tastebridge.dispatch

import com.jammking.tastebridge.model.Review

interface TasteCompassDispatcher {
    fun postReviews(reviews: List<Review>): List<String>
}