package com.example.auth.basic.service

import com.example.auth.basic.dto.Product
import com.example.auth.basic.entity.UserInfo
import com.example.auth.basic.repository.UserInfoRepository
import jakarta.annotation.PostConstruct
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.stream.Collectors
import java.util.stream.IntStream
import kotlin.random.Random


@Service
class ProductService(
    val repository: UserInfoRepository,
    val passwordEncoder: PasswordEncoder
) {

    var productList: List<Product> = listOf()

    @PostConstruct
    fun loadProducts() {
        productList = IntStream.range(1, 100).mapToObj {
            Product(
                it, "product$it", Random.nextInt(10), Random.nextDouble(
                    1000.0
                )
            )
        }.collect(Collectors.toList())
    }

    fun getProducts(): List<Product> {
        return productList
    }

    fun getProduct(id: Int): Product {
        return productList.stream().filter { p -> p.productId == id }.findAny().get()
    }

    fun addUser(userInfo: UserInfo): String {
        userInfo.password = passwordEncoder.encode(userInfo.password)
        repository.save(userInfo)
        return "user added to system "
    }
}