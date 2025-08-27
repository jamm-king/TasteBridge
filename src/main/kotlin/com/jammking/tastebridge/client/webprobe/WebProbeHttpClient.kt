package com.jammking.tastebridge.client.webprobe

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.jammking.tastebridge.config.AppProperties
import com.jammking.tastebridge.model.CrawledPage
import com.jammking.tastebridge.model.WebProbeRequest
import com.jammking.tastebridge.model.WebProbeResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

private val JSON = "application/json; charset=utf-8".toMediaType()

@Component
class WebProbeHttpClient(
    private val props: AppProperties,
    private val client: OkHttpClient,
    private val om: ObjectMapper
): WebProbeClient {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun crawl(req: WebProbeRequest): List<CrawledPage> {
        val url = props.webprobeBaseUrl.trimEnd('/') + "/crawl"
        val body = om.writeValueAsString(req).toRequestBody(JSON)
        val httpReq = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(httpReq).execute().use { resp ->
            if(!resp.isSuccessful) {
                val msg = resp.body?.string()
                log.warn("WebProbe crawl failed: status={} body = {} ", resp.code, msg)
                throw IllegalStateException("WebProbe crawl failed: ${resp.code}")
            }

            val respBody = resp.body?.string() ?: "{}"
            val parsed = om.readValue(respBody, WebProbeResponse::class.java)

            return parsed.pages
        }
    }
}