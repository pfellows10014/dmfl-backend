package com.dmfl.backendserver.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class WebSecurityConfig {

    @Value("\${crossOriginsUrls}")
    lateinit var crossOriginsUrlList: List<String>

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = crossOriginsUrlList
        configuration.allowedMethods = listOf("GET", "POST")
        configuration.allowedHeaders = listOf("*")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/api/**", configuration)
        return source
    }

    @Bean
    fun filterChainBasic(http: HttpSecurity): SecurityFilterChain {
        http {
            cors {
                corsConfigurationSource()
            }
            authorizeRequests {
                authorize("/api/**", permitAll)
//                authorize("POST", hasAnyAuthority("DMFL_ADMIN"))
            }
            httpBasic {}
        }
        return http.build()
    }
}