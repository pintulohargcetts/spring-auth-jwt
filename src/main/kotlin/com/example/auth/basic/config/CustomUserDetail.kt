package com.example.auth.basic.config

import com.example.auth.basic.entity.UserInfo
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.stream.Collectors


class CustomUserDetail(userInfo: UserInfo) : UserDetails {
    private val name: String
    private val password: String
    private val authorities: List<GrantedAuthority>

    init {
        name = userInfo.name
        password = userInfo.password
        authorities = (userInfo.role.split(",").stream()
            .map { role: String? -> SimpleGrantedAuthority(role) }
            .collect(Collectors.toList()))
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return name
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}