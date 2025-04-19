package ru.hse.jwtstarter

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.filter.OncePerRequestFilter
import ru.hse.jwtstarter.jwt.filter.JwtAuthenticationFilter
import ru.hse.jwtstarter.jwt.matcher.JwtRequestMatcher
import ru.hse.jwtstarter.jwt.properties.JwtProperties
import ru.hse.jwtstarter.jwt.service.JwtService

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class JwtStarterAutoConfiguration {
    @Bean
    fun jwtFilter(
        jwtRequestMatcher: RequestMatcher,
        jwtService: JwtService,
    ): OncePerRequestFilter = JwtAuthenticationFilter(jwtRequestMatcher, jwtService)


    @Bean
    fun jwtRequestMatcher(
    ): RequestMatcher = JwtRequestMatcher()

    @Bean
    @ConditionalOnMissingBean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun jwtService(
        jwtProperties: JwtProperties
    ) = JwtService(jwtProperties)
}
