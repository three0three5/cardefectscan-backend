package ru.hse.jwtstarter

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.RequestMatcher
import ru.hse.jwtstarter.jwt.filter.JwtAuthenticationFilter
import ru.hse.jwtstarter.jwt.matcher.JwtRequestMatcher
import ru.hse.jwtstarter.jwt.properties.JwtProperties
import ru.hse.jwtstarter.jwt.provider.JwtAuthenticationProvider
import ru.hse.jwtstarter.jwt.service.JwtService

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class JwtStarterAutoConfiguration {
    @Bean
    fun jwtFilter(
        authenticationManager: AuthenticationManager,
        jwtRequestMatcher: RequestMatcher,
    ): AbstractAuthenticationProcessingFilter = JwtAuthenticationFilter(jwtRequestMatcher, authenticationManager)


    @Bean
    fun jwtRequestMatcher(
    ): RequestMatcher = JwtRequestMatcher()

    @Bean
    fun authenticationManager(
        jwtAuthenticationProvider: JwtAuthenticationProvider,
    ) = ProviderManager(jwtAuthenticationProvider)

    @Bean
    fun jwtAuthenticationProvider(
        jwtService: JwtService,
    ) = JwtAuthenticationProvider(jwtService)

    @Bean
    @ConditionalOnMissingBean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun jwtService(
        jwtProperties: JwtProperties
    ) = JwtService(jwtProperties)
}
