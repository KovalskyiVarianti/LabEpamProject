package com.example.labepamproject.data.network

import com.squareup.moshi.Json

data class PokemonListResponse(
    val count: Int,
    val results: List<PokemonPartialResponse>,
)

data class PokemonPartialResponse(
    val name: String,
    val url: String,
)

data class PokemonDetailedResponse(
    val id: Int,
    val name: String,
    @field:Json(name = "base_experience") val experience: Int,
    val height: Int,
    val weight: Int,
    val abilities: List<PokemonAbilityData>,
    val stats: List<PokemonStatsData>,
    val types: List<PokemonTypesData>,
)