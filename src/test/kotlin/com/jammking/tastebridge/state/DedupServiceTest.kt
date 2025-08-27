package com.jammking.tastebridge.state

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.dao.DuplicateKeyException

class DedupServiceTest {

    private val repo = mockk<DedupUrlRepository>(relaxed = true)
    private val service = DedupService(repo)

    @Test
    fun `returns true when insert succeeds`() {
        every { repo.insert(any<DedupUrl>()) } answers { firstArg() }
        val ok = service.markIfNew("hash1", "https://x")
        assertTrue(ok)
    }

    @Test
    fun `returns false when duplicate`() {
        every { repo.insert(any<DedupUrl>()) } throws DuplicateKeyException("dup")
        val ok = service.markIfNew("hash1", "https://x")
        assertFalse(ok)
    }
}