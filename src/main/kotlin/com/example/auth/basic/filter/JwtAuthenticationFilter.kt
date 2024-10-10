package com.example.auth.basic.filter

import com.example.auth.basic.config.UserInfoUserDetailsService
import com.example.auth.basic.service.JwtAuthService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    val jwtAuthService: JwtAuthService,
    val userInfoUserDetailsService: UserInfoUserDetailsService
) :OncePerRequestFilter () {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        var token = ""
        if(authHeader!= null && authHeader.startsWith("Bearer")){
            token = authHeader.substring(7)
            logger.info("bearer token $token and header:$authHeader")
            val username = jwtAuthService.getUserNameFromToken(token)

            logger.info("bearer token $token and header:$authHeader")
            if(username!=null && SecurityContextHolder.getContext().authentication == null){
                val userDeails = userInfoUserDetailsService.loadUserByUsername(username)
                if(jwtAuthService.validateToken(token,userDeails)){
                    val usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken(userDeails,null,userDeails.authorities)
                    usernamePasswordAuthenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
                    logger.info("bearer token $token  valid")
                }else{
                    logger.info("bearer token $token not valid")
                }
                // check if token is valid
            }
        }
        filterChain.doFilter(request,response)
    }
}