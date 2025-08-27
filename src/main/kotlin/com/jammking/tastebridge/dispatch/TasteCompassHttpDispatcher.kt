package com.jammking.tastebridge.dispatch

import com.fasterxml.jackson.databind.ObjectMapper
import com.jammking.tastebridge.config.AppProperties
import com.jammking.tastebridge.model.Review
import com.jammking.tastebridge.util.HashUtil
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

private val JSON = "application/json; charset=utf-8".toMediaType()

@Component
class TasteCompassHttpDispatcher(
    private val props: AppProperties,
    private val client: OkHttpClient,
    private val om: ObjectMapper
): TasteCompassDispatcher {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun postReviews(reviews: List<Review>): List<String> {
        if(reviews.isEmpty()) return emptyList()
        val url = props.tasteCompassEndpoint
        val created = mutableListOf<String>()

        reviews.forEach { r->
            val payload = om.writeValueAsString(r).toRequestBody(JSON)
            val idemKey = HashUtil.sha256Hex(r.url + (r.metadata["title"] ?: ""))
            val req = Request.Builder()
                .url(url)
                .post(payload)
                .addHeader("X-Idempotency-Key", idemKey)
                .build()

            client.newCall(req).execute().use { resp ->
                if(!resp.isSuccessful) {
                    val msg = resp.body?.string()
                    log.warn("TasteCompass post failed: status={} body={} ", resp.code, msg)
                    throw IllegalStateException("TasteCompass post failed: ${resp.code}")
                }

                created += idemKey
            }
        }

        return created
    }

}