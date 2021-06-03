package com.example.labepamproject.data.network.dto

import com.squareup.moshi.Json

data class PokemonSpeciesResponse(
    val name: String,
    @field:Json(name = "evolution_chain") val evolutionChain: EvolutionChainPartialResponse,
)

data class EvolutionChainPartialResponse(
    val url: String
)

data class EvolutionChainResponse(
    val chain: ChainResponse,
)

data class ChainResponse(
    @field:Json(name = "evolves_to") val nextChain: List<ChainResponse?>,
    val species: PokemonPartialResponse,
)
