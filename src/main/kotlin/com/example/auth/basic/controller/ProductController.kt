package com.example.auth.basic.controller

import com.example.auth.basic.dto.JwtAuthRequest
import com.example.auth.basic.dto.Product
import com.example.auth.basic.entity.UserInfo
import com.example.auth.basic.service.JwtAuthService
import com.example.auth.basic.service.ProductService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/")
class ProductController(
    val service: ProductService,
    val jwtAuthService: JwtAuthService,
    val authenticationManager: AuthenticationManager
) {
    private val logger: Logger = LoggerFactory.getLogger(ProductController::class.java)

    @GetMapping("/hello")
    fun welcome(): String {
        logger.info("this is not secure endpoint")
        return "Welcome this endpoint is not secure"
    }

    @PostMapping("/token")
    fun authenticateAndGetToken(@RequestBody jwtAuthRequest: JwtAuthRequest): ResponseEntity<String>{
        val authentication = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(jwtAuthRequest.password,jwtAuthRequest.password))
        if(authentication.isAuthenticated){
            return ResponseEntity.ok(jwtAuthService.generateToken(jwtAuthRequest.username))
        }else{
            throw UsernameNotFoundException("Invalid user")
        }
    }

    /**
     * Add new user { "name":"guest", "password":"guest", "role":
     * "ROLE_USER,ROLE_ADMIN" }
     *
     * @param userInfo
     * @return
     */

    @PostMapping("/adduser")
    fun addNewUser(@RequestBody userInfo: UserInfo): String {
        return service.addUser(userInfo)
    }

    @GetMapping("/product/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    fun getAllTheProducts(): List<Product> {
        return service.getProducts()
    }

    @GetMapping("/product/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    fun getProductById(@PathVariable id: Int): Product {
        return service.getProduct(id)
    }


}