package com.example.auth.basic.repository

import com.example.auth.basic.entity.UserInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserInfoRepository : JpaRepository<UserInfo, Integer> {
    fun findByName(name: String): UserInfo
}
