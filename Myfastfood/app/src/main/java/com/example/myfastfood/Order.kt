package com.example.myfastfood

data class Order(
    val mainMeal: String? = null,
    val sideDishes: Set<String> = emptySet(),
    val drink: String? = null,
    val isSubmitted: Boolean = false
)
