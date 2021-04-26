package com.example.labepamproject.domain


data class PokemonEntity(
    val id: Int,
    val name: String,
    val prevImgUrl: String,
    val experience: Int,
    val height: Int,
    val weight: Int,
    val abilities: List<String>,
    val stats: List<Pair<String, Int>>,
    val types: List<String>,
)
