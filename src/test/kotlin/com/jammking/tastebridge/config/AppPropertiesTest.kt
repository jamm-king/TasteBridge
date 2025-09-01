package com.jammking.tastebridge.config

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.context.properties.bind.BindException
import org.springframework.boot.context.properties.bind.validation.BindValidationException
import org.springframework.boot.test.context.runner.ApplicationContextRunner

class AppPropertiesTest {

    private val ctxRunner = ApplicationContextRunner()
        .withUserConfiguration(TestConfig::class.java)

    @EnableConfigurationProperties(AppProperties::class)
    private class TestConfig

    @Test
    fun `binds values successfully`() {
        ctxRunner.withPropertyValues(
            "app.webprobe-base-url=http://wp:8080",
            "app.webprobe-crawl-path=/api/crawl",
            "app.taste-compass-endpoint=http://tc:8080/api/reviews",
            "app.jobs.keywords[0]=a",
            "app.jobs.max-results=10",
            "app.jobs.fresh=true",
            "app.jobs.cron=0 */10 * * * ?"
        ).run { ctx ->
            val props = ctx.getBean(AppProperties::class.java)
            assertEquals("http://wp:8080", props.webprobeBaseUrl)
            assertEquals("http://tc:8080/api/reviews", props.tasteCompassEndpoint)
            assertEquals(10, props.jobs.maxResults)
        }
    }

    @Test
    fun `fails when mandatory properties are missing`() {
        ctxRunner.run { ctx ->
            assertNotNull(ctx.startupFailure)
            assertTrue(ctx.startupFailure.cause is BindException)
        }
    }

    @Test
    fun `fails when mandatory properties are blank strings`() {
        ctxRunner.withPropertyValues(
            "app.webprobe.base-url=",
            "app.webprobe.crawl-path=",
            "app.taste-compass-endpoint=",
            "app.jobs.max-results=10",
        ).run { ctx ->
            assertNotNull(ctx.startupFailure)
            assertTrue(ctx.startupFailure.cause is BindException)
        }
    }
}