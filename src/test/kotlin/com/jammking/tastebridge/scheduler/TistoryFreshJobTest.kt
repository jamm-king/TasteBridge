package com.jammking.tastebridge.scheduler

import com.jammking.tastebridge.client.webprobe.WebProbeClient
import com.jammking.tastebridge.config.AppProperties
import com.jammking.tastebridge.dispatch.TasteCompassDispatcher
import com.jammking.tastebridge.model.CrawledPage
import com.jammking.tastebridge.model.Review
import com.jammking.tastebridge.model.WebProbeRequest
import com.jammking.tastebridge.state.DedupService
import com.jammking.tastebridge.state.JobRunService
import com.jammking.tastebridge.state.RunStats
import com.jammking.tastebridge.transform.PageToReviewTransformer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TistoryFreshJobTest {

    private lateinit var job: TistoryFreshJob

    private val props = AppProperties(
        webprobeBaseUrl = "http://webprobe.local",
        tasteCompassEndpoint = "http://tastecompass.local/api/reviews",
        jobs = AppProperties.Jobs(keywords = listOf("포항 식당 리뷰"), maxResults = 30, fresh = true)
    )

    private val webProbe = mockk<WebProbeClient>()
    private val transformer = mockk<PageToReviewTransformer>()
    private val dispatcher = mockk<TasteCompassDispatcher>()
    private val dedup = mockk<DedupService>()
    private val jobRuns = mockk<JobRunService>()

    @BeforeEach
    fun setup() {
        job = TistoryFreshJob(props, webProbe, transformer, dispatcher, dedup, jobRuns)
    }

    @Test
    fun `run processes new pages and posts reviews`() {
        every { jobRuns.start(any()) } returns "run-1"
        every { jobRuns.finish(eq("run-1"), any<RunStats>()) } returns Unit

        val pages = listOf(
            CrawledPage(url = "https://t1", text = "a".repeat(60)),
            CrawledPage(url = "https://t2", text = "b".repeat(60))
        )
        every { webProbe.crawl(any<WebProbeRequest>()) } returns pages

        every { dedup.markIfNew(any(), any()) } returnsMany listOf(true, false)
        every { transformer.toReview(any()) } returns Review(url = "https://t1", text = "x".repeat(60))
        every { dispatcher.postReviews(any()) } returns listOf("id1")

        job.run()

        verify(exactly = 1) { dispatcher.postReviews(match { it.size == 1 && it[0].url == "https://t1" }) }
        verify { jobRuns.start("tistory-fresh") }
        verify { jobRuns.finish(eq("run-1"), any<RunStats>()) }
    }
}