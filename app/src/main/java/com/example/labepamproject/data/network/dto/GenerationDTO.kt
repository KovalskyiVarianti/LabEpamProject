package com.example.labepamproject.data.network.dto

import com.example.labepamproject.domain.entity.GenerationEntity
import com.example.labepamproject.presentation.getGenerationId
import com.squareup.moshi.Json
import java.util.*

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
    val name: String,
    @field:Json(name = "pokemon_species") val pokemons: List<PokemonPartialResponse>
)


fun GenerationDetailedResponse.asEntity() =
    GenerationEntity(getGenerationId(name), name.toUpperCase(Locale.ROOT), pokemons.map { it.name })
