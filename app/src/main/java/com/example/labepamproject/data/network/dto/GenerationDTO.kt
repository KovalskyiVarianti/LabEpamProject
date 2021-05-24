package com.example.labepamproject.data.network

import com.squareup.moshi.Json

data class GenerationListResponse(
    val count: Int,
    val results: List<GenerationPartialResponse>,
)

data class GenerationPartialResponse(
    val name: String,
    val url: String,
)

data class GenerationDetailedResponse(
    val id: Int,
    @field:Json(name = "pokemon_species") val pokemons: List<PokemonPartialResponse>
)