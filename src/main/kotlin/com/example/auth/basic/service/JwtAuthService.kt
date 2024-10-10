package com.example.auth.basic.service

import com.example.auth.basic.controller.ProductController
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtAuthService {
    private val logger: Logger = LoggerFactory.getLogger(JwtAuthService::class.java)
    private val secret: String = "ec283985c55e98bf801f08aa1d54009394e5490398067fa9a72088efbb22a5c73141b2bc201fcacca8447ea74495c0ca1e992178b4a46436184cca66e5c0936c32cea5c3ef8f3c356df1df9b8f09587c4843d525e6911ed6475708c06a432ece102abca9e32f2cef1c59417cccdc59752916d644d39d2fafc926c3a0f06018dc"
    fun generateToken(username: String): String {
        val claims: Map<String, Any> = HashMap();
        return createToken(claims, username)
    }

    private fun createToken(claims: Map<String, Any>, username: String): String {
        return Jwts.builder()
            .subject(username)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(Date(System.currentTimeMillis()).time + 1000 * 60 * 30)) // for 30 min.
            .signWith(this.getSigningKey(), Jwts.SIG.HS256).compact()
    }

    // https://lenagend.tistory.com/17
    private fun getSigningKey(): SecretKey {
        val keyInBytes: ByteArray = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyInBytes)
    }

    fun getClaimsFromToken(token:String): Claims{
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).payload
    }

    fun getUserNameFromToken(token:String): String{
        val claims  = getClaimsFromToken(token)
        logger.info("getting username from token ${claims.subject}, ${claims.issuedAt}")
        return claims.subject
    }

    fun isTokenExpired(token:String): Boolean{
        val claims  = getClaimsFromToken(token)
        return claims.expiration.before(Date(System.currentTimeMillis()))
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean{
        val userNameFromToken = getUserNameFromToken(token)
        return userNameFromToken!=null && userDetails.username.contentEquals(userNameFromToken) && !isTokenExpired(token)
    }
}