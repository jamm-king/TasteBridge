package com.jammking.tastebridge.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
data class AppProperties(
    val webprobeBaseUrl: String,
    val tasteCompassEndpoint: String,
    val tasteCompassApiKey: String,
    val jobs: Jobs
) {
    data class Jobs(
        val keywords: List<String> = emptyList(),
        val maxResults: Int = 20,
        val fresh: Boolean = true,
        val cron: String = "0 */30 * * * ?"
    )
}
