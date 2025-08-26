package com.jammking.tastebridge.state

import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@Repository
interface DedupUrlRepository: MongoRepository<DedupUrl, String>

@Service
class DedupService(
    private val repo: DedupUrlRepository
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun markIfNew(urlHash: String, url: String): Boolean = try {
        repo.insert(DedupUrl(id = urlHash, url = url))
        true
    } catch(_: DuplicateKeyException) {
        false
    } catch(e: Exception) {
        log.warn("Dedup insert failed: $urlHash")
        false
    }

}