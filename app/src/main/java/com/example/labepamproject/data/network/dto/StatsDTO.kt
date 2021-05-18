package com.example.labepamproject.data.network

import com.squareup.moshi.Json

data class PokemonStatsData(
    @field:Json(name = "base_stat") val base_stat: Int,
    val effort: Int,
    val stat: PokemonStatsDetailsData,
)

data class PokemonStatsDetailsData(
    val name: String,
    val url: String,
)