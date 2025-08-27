package com.jammking.tastebridge.state

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("job_runs")
data class JobRun(
    @Id val id: String,
    val jobId: String,
    val startedAt: Instant,
    val finishedAt: Instant? = null,
    val stats: RunStats = RunStats()
)

data class RunStats(
    val collected: Int = 0,
    val transformed: Int = 0,
    val posted: Int = 0,
    val failed: Int = 0
)