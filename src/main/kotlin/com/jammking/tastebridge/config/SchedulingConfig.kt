package com.jammking.tastebridge.config

import net.javacrumbs.shedlock.core.LockProvider
import net.javacrumbs.shedlock.provider.mongo.MongoLockProvider
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
@EnableConfigurationProperties(AppProperties::class)
class SchedulingConfig {
    @Bean
    fun lockProvider(mongoTemplate: MongoTemplate): LockProvider = MongoLockProvider(mongoTemplate.db)
}