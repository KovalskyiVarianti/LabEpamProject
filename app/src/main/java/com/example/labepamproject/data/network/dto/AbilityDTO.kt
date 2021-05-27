package com.example.labepamproject.data.network.dto

import com.squareup.moshi.Json

data class PokemonAbilityDetailsData(
    val name: String,
    val url: String,
)

data class PokemonAbilityData(
    val ability: PokemonAbilityDetailsData,
    @field:Json(name = "is_hidden") val isHidden: Boolean,
    val slot: Int,
)