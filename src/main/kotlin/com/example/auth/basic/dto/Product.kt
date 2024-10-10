package com.example.auth.basic.dto

data class Product(
    val productId: Int = 0,
    val name: String? = null,
    val qty: Int = 0,
    val price: Double = 0.0,
)