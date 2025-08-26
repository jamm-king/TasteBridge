package com.jammking.tastebridge.state

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Repository
interface JobRunRepository: MongoRepository<JobRun, String>

@Service
class JobRunService(
    private val repo: JobRunRepository
) {
    fun start(jobId: String): String {
        val runId = UUID.randomUUID().toString()
        repo.insert(JobRun(id = runId, jobId = jobId, startedAt = Instant.now()))
        return runId
    }

    fun finish(runId: String, stats: RunStats) {
        val entity = repo.findById(runId).orElse(null) ?: return
        repo.save(entity.copy(finishedAt = Instant.now(), stats = stats))
    }
}