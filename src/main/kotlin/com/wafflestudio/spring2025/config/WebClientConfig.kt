package com.wafflestudio.spring2025.config

import io.netty.channel.ChannelOption
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration

@Configuration
class WebClientConfig(
    @Value("\${sugang.base-url}") private val baseUrl: String,
    @Value("\${sugang.connect-timeout-ms:5000}") private val connectTimeoutMS: Int,
    @Value("\${sugang.read-timeout-ms:15000}") private val readTimeoutMS: Int,
) {
    @Bean
    fun sugangWebClient(): WebClient {
        val httpClient =
            HttpClient
                .create()
                .option(
                    ChannelOption.CONNECT_TIMEOUT_MILLIS,
                    connectTimeoutMS,
                ).responseTimeout(Duration.ofMillis(readTimeoutMS.toLong()))
        return WebClient
            .builder()
            .baseUrl(
                baseUrl,
            ).defaultHeader("User-Agent", "Mozilla/5.0")
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .codecs { cfg ->
                cfg.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)
            }.build()
    }
}
