package com.example.labepamproject.data.network.dto

import com.example.labepamproject.domain.PokemonEntity
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

fun PokemonDetailedResponse.toEntity() =
    PokemonEntity(
        id = id,
        name = name.replaceFirst(name[0], name[0].toUpperCase()),
        prevImgUrl = generateUrlFromId(id),
        experience = experience,
        height = height,
        weight = weight,
        abilities = abilities.map { it.ability.name },
        stats = stats.map { it.stat.name to it.base_stat },
        types = types.map { it.type.name }
    )

private fun generateUrlFromId(id: Int): String =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"