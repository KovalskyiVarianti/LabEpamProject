package com.example.labepamproject.domain


data class Pokemon(
    val id: Int,
    val name: String,
    val prevImgUrl: String,
    val experience: Int = 0,
    val height: Int = 0,
    val weight: Int = 0,
)
