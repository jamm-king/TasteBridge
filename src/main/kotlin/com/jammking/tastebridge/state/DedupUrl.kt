package com.jammking.tastebridge.state

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("dedup_urls")
data class DedupUrl(
    @Id val id: String,
    val url: String,
    @Indexed(expireAfter = "30d")
    val createdAt: Instant = Instant.now()
)