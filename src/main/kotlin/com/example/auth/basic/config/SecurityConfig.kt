package com.example.auth.basic.config

import com.example.auth.basic.filter.JwtAuthenticationFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {
    @Autowired
    private  lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter
    @Bean //authentication
    fun userDetailsService(): UserDetailsService {
        /*  val admin: UserDetails = User.withUsername("admin")
              .password(passwordEncoder().encode("pwd1")).roles("ADMIN").build()
          val user: UserDetails = User.withUsername("user")
              .password(passwordEncoder().encode("pwd2")).roles("USER")
              .build()
          return InMemoryUserDetailsManager(admin, user)*/
        return UserInfoUserDetailsService()
    }

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http.csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/hello", "/adduser","/token").permitAll().requestMatchers("/product/**")
                    .authenticated()
            }.sessionManagement{
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }.authenticationProvider(authenticationProvider()).addFilterBefore(jwtAuthenticationFilter,UsernamePasswordAuthenticationFilter::class.java).build()
    // use it before UsernamePasswordAuthenticationFilter

        // Security Internal flow .. Request ---> Filter(UsernamePasswordAuthenticationFilter) --> AuthenticationManager --> AuthenticationProvider--> UserDetailsService
        // with Above config we are telling authentication provider to JwtAuthenticationFilter aheader of  jwtAuthenticationFilter
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    // https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/dao-authentication-provider.html
    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val daoAuthenticationProvider = DaoAuthenticationProvider()
        daoAuthenticationProvider.setUserDetailsService(userDetailsService())
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder())
        return daoAuthenticationProvider
    }

    @Bean
    fun authenticationManager(authenticationConfig: AuthenticationConfiguration): AuthenticationManager{
        return authenticationConfig.authenticationManager
    }
}