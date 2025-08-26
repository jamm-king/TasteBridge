package com.jammking.tastebridge.model

data class Review(
    val source: String = "tistory",
    val url: String,
    val text: String,
    val x: Double? = null,
    val y: Double? = null,
    val metadata: Map<String, Any?> = emptyMap()
)