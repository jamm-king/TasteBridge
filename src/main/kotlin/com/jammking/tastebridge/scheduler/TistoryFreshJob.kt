package com.jammking.tastebridge.scheduler

import com.jammking.tastebridge.client.webprobe.WebProbeClient
import com.jammking.tastebridge.config.AppProperties
import com.jammking.tastebridge.dispatch.TasteCompassDispatcher
import com.jammking.tastebridge.model.WebProbeRequest
import com.jammking.tastebridge.state.DedupService
import com.jammking.tastebridge.state.JobRunService
import com.jammking.tastebridge.state.RunStats
import com.jammking.tastebridge.transform.PageToReviewTransformer
import com.jammking.tastebridge.util.HashUtil
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class TistoryFreshJob(
    private val props: AppProperties,
    private val webProbe: WebProbeClient,
    private val transformer: PageToReviewTransformer,
    private val dispatcher: TasteCompassDispatcher,
    private val dedup: DedupService,
    private val jobRuns: JobRunService
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Scheduled(cron = "\${app.jobs.cron}")
    @SchedulerLock(name = "tistoryFreshJob", lockAtMostFor = "PT10M", lockAtLeastFor = "PT1M")
    fun run() {
        val runId = jobRuns.start("tistory-fresh")
        var collected = 0; var transformed = 0; var posted = 0; var failed = 0

        try {
            val req = WebProbeRequest(
                userId = "tastecompass",
                keywords = props.jobs.keywords,
                engines = listOf("TISTORY"),
                maxResults = props.jobs.maxResults,
                fresh = props.jobs.fresh
            )

            val pages = webProbe.crawl(req)
            collected = pages.size

            pages.forEach { p ->
                val urlHash = HashUtil.sha256Hex(p.url)
                if(!dedup.markIfNew(urlHash, p.url)) return@forEach

                val review = transformer.toReview(p) ?: return@forEach
                try {
                    dispatcher.postReviews(listOf(review))
                    transformed++
                    posted++
                } catch(e: Exception) {
                    failed++
                    log.warn("dispatch failed for url={}", p.url, e)
                }
            }
        } finally {
            jobRuns.finish(runId, RunStats(collected, transformed, posted, failed))
            log.info("tistoryFreshJob finished runId={} stats={}/{}/{} failed={}", runId, collected, transformed, posted, failed)
        }
    }
}