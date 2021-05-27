package com.example.labepamproject.data.network.dto

data class PokemonTypesData(
    val slot: Int,
    val type: PokemonTypesDetailsData,
)

data class PokemonTypesDetailsData(
    val name: String,
    val url: String,
)
