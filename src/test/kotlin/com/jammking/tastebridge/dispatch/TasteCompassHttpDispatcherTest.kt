package com.jammking.tastebridge.dispatch

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jammking.tastebridge.config.AppProperties
import com.jammking.tastebridge.model.Review
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TasteCompassHttpDispatcherTest {

    private lateinit var server: MockWebServer
    private lateinit var dispatcher: TasteCompassHttpDispatcher

    @BeforeEach
    fun setup() {
        server = MockWebServer()
        server.start()

        val baseUrl = server.url("/api/reviews").toString()
        val props = AppProperties(
            webprobeBaseUrl = "http://webprobe.local",
            webprobeCrawlPath = "/api/crawl",
            tasteCompassEndpoint = baseUrl,
            jobs = AppProperties.Jobs()
        )
        dispatcher = TasteCompassHttpDispatcher(props, OkHttpClient(), jacksonObjectMapper())
    }

    @AfterEach
    fun cleanup() {
        server.shutdown()
    }

    @Test
    fun `posts each review with headers and body`() {
        server.enqueue(MockResponse().setResponseCode(200).setBody("ok"))

        val review = Review(
            url = "https://tistory/10",
            text = "a".repeat(60),
            metadata = mapOf("title" to "맛집")
        )
        val ids = dispatcher.postReviews(listOf(review))
        assertEquals(1, ids.size)

        val recorded = server.takeRequest()
        assertEquals("POST", recorded.method)
        assertTrue(recorded.path!!.startsWith("/api/reviews"))
        assertNotNull(recorded.getHeader("X-Idempotency-Key"))
        val body = recorded.body.readUtf8()
        assertTrue(body.contains("tistory"))
        assertTrue(body.contains("맛집"))
    }
}