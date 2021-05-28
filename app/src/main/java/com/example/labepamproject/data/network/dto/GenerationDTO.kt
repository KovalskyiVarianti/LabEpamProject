package com.example.labepamproject.data.network.dto

import com.example.labepamproject.domain.GenerationEntity
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
    @field:Json(name = "pokemon_species") val pokemons: List<PokemonPartialResponse>
)


fun GenerationPartialResponse.asEntity() =
    GenerationEntity(getGenerationId(name), name.toUpperCase(Locale.ROOT))

private fun getGenerationId(text: String) = when (text) {
    "generation-i" -> 1
    "generation-ii" -> 2
    "generation-iii" -> 3
    "generation-iv" -> 4
    "generation-v" -> 5
    "generation-vi" -> 6
    "generation-vii" -> 7
    "generation-viii" -> 8
    else -> 0
}