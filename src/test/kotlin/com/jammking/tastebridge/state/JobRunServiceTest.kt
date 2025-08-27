package com.jammking.tastebridge.state

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.time.Instant
import java.util.*

class JobRunServiceTest {

    private val repo = mockk<JobRunRepository>(relaxed = true)
    private val service = JobRunService(repo)

    @Test
    fun `start creates new rn with uuid`() {
        every { repo.insert(any<JobRun>()) } answers { firstArg() }
        val runId = service.start("job1")
        assert(runId.isNotBlank())
    }

    @Test
    fun `finish updates stats`() {
        val existing = JobRun(id = "rid", jobId = "job1", startedAt = Instant.now())
        every { repo.findById("rid") } returns Optional.of(existing)
        val saved = slot<JobRun>()
        every { repo.save(capture(saved)) } answers { saved.captured }

        val stats = RunStats(collected = 3, transformed = 2, posted = 1, failed = 1)
        service.finish("rid", stats)

        verify { repo.save(any()) }
        assertEquals(stats, saved.captured.stats)
        assert(saved.captured.finishedAt != null)
    }
}