package com.jammking.tastebridge.config

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties(prefix = "app")
data class AppProperties(
    @field:NotBlank val webprobeBaseUrl: String,
    @field:NotBlank val webprobeCrawlPath: String,
    @field:NotBlank val tasteCompassEndpoint: String,
    val jobs: Jobs
) {
    data class Jobs(
        val keywords: List<String> = emptyList(),
        val maxResults: Int = 20,
        val fresh: Boolean = true,
        val cron: String = "0 */30 * * * ?"
    )
}
