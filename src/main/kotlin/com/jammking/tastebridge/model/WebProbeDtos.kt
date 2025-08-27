package com.jammking.tastebridge.model

data class WebProbeRequest(
    val userId: String,
    val keywords: List<String>,
    val engines: List<String> = listOf("TISTORY"),
    val maxResults: Int = 20,
    val fresh: Boolean = true
)

data class WebProbeResponse(
    val pages: List<CrawledPage>
)

data class CrawledPage(
    val url: String,
    val title: String? = null,
    val html: String? = null,
    val text: String? = null
)