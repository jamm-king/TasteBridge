package com.jammking.tastebridge.client.webprobe

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jammking.tastebridge.config.AppProperties
import com.jammking.tastebridge.model.WebProbeRequest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class WebProbeHttpClientTest {

    private lateinit var server: MockWebServer
    private lateinit var client: WebProbeHttpClient

    @BeforeEach
    fun setup() {
        server = MockWebServer()
        server.start()
        val props = AppProperties(
            webprobeBaseUrl = server.url("/").toString().trimEnd('/'),
            tasteCompassEndpoint = "http://tastecompass.local.api/reviews",
            jobs = AppProperties.Jobs()
        )
        client = WebProbeHttpClient(props, OkHttpClient(), jacksonObjectMapper())
    }

    @AfterEach
    fun cleanup() {
        server.shutdown()
    }

    @Test
    fun `parse array response`() {
        val json = """
            {"pages":[{"url":"https://t1","text":"${"a".repeat(60)}"}]}
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(200).setBody(json))

        val pages = client.crawl(WebProbeRequest("u", listOf("k")))
        assertEquals(1, pages.size)
        assertEquals("https://t1", pages[0].url)
    }

    @Test
    fun `parse object pages response`() {
        val json = """
            {"pages":[{"url":"https://t2","text":"${"b".repeat(60)}"}]}
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(200).setBody(json))

        val pages = client.crawl(WebProbeRequest("u", listOf("k")))
        assertEquals(1, pages.size)
        assertEquals("https://t2", pages[0].url)
    }
}