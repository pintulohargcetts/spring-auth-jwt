package com.example.auth.basic.config

import com.example.auth.basic.repository.UserInfoRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component

@Component
class UserInfoUserDetailsService : UserDetailsService {
    private val logger: Logger = LoggerFactory.getLogger(UserInfoUserDetailsService::class.java)

    @Autowired
    private lateinit var repository: UserInfoRepository
    override fun loadUserByUsername(username: String): UserDetails {
        val userInfo = repository.findByName(username)
        logger.info("loading user:${userInfo.name} role : ${userInfo.role} passwd: ${userInfo.password}")
        return CustomUserDetail(userInfo)
    }
}