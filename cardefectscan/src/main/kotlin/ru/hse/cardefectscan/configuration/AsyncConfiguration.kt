package ru.hse.cardefectscan.configuration

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import ru.hse.cardefectscan.properties.AsyncProperties
import java.util.concurrent.Executor

@EnableAsync
@Configuration
@EnableConfigurationProperties(AsyncProperties::class)
class AsyncConfiguration(
    private val asyncProperties: AsyncProperties,
) {
    @Bean(MODEL_CLIENT_EXECUTOR)
    fun asyncExecutor(): Executor {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = asyncProperties.modelClient.corePoolSize
            maxPoolSize = asyncProperties.modelClient.maxPoolSize
            queueCapacity = asyncProperties.modelClient.queueCapacity
            setThreadNamePrefix("$MODEL_CLIENT_EXECUTOR-")
            initialize()
        }
    }

    companion object {
        const val MODEL_CLIENT_EXECUTOR = "model_client_executor"
    }
}