package com.jammking.tastebridge.client.webprobe

import com.jammking.tastebridge.model.CrawledPage
import com.jammking.tastebridge.model.WebProbeRequest

interface WebProbeClient {
    fun crawl(req: WebProbeRequest): List<CrawledPage>
}