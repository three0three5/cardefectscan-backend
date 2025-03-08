package ru.hse.cardefectscan.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

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
}