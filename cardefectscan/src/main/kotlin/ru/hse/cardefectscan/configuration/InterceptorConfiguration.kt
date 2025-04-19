package ru.hse.cardefectscan.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import ru.hse.cardefectscan.service.security.ClientDataInterceptor
import ru.hse.cardefectscan.service.security.CookieInterceptor

@Configuration
class InterceptorConfiguration(
    private val cookieInterceptor: CookieInterceptor,
    private val clientDataInterceptor: ClientDataInterceptor,
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(cookieInterceptor)
        registry.addInterceptor(clientDataInterceptor)
    }
}