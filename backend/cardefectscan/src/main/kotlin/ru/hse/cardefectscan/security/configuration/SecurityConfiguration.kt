package ru.hse.cardefectscan.security.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.RequestMatcher
import ru.hse.cardefectscan.security.filter.JwtAuthenticationFilter
import ru.hse.cardefectscan.security.matcher.JwtRequestMatcher
import ru.hse.cardefectscan.security.provider.JwtAuthenticationProvider
import ru.hse.cardefectscan.security.service.JwtService

@Configuration
@EnableWebSecurity
class SecurityConfiguration {
    @Bean
    fun securityWebFilterChain(
        http: HttpSecurity,
        jwtFilter: AbstractAuthenticationProcessingFilter,
    ): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/api/v1/auth/**").permitAll()
                    .anyRequest().authenticated()
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }

    @Bean
    fun jwtFilter(
        authenticationManager: AuthenticationManager,
        jwtRequestMatcher: RequestMatcher,
    ): AbstractAuthenticationProcessingFilter {
        return JwtAuthenticationFilter(jwtRequestMatcher, authenticationManager)
    }

    @Bean
    fun jwtRequestMatcher(): RequestMatcher {
        return JwtRequestMatcher()
    }

    @Bean
    fun authenticationManager(
        jwtAuthenticationProvider: JwtAuthenticationProvider,
    ) = ProviderManager(jwtAuthenticationProvider)

    @Bean
    fun jwtAuthenticationProvider(
        jwtService: JwtService,
    ) = JwtAuthenticationProvider(jwtService)

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()
}