package com.example.labepamproject.domain


data class PokemonEntity(
    val id: Int,
    val name: String,
    val prevImgUrl: String,
    val experience: Float,
    val height: Int,
    val weight: Int,
    val abilities: List<String>,
    val stats: List<Pair<String, Float>>,
    val types: List<String>,
)
