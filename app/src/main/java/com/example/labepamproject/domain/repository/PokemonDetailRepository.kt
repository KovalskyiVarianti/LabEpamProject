package com.example.labepamproject.domain.repository

import com.example.labepamproject.domain.Result
import com.example.labepamproject.domain.entity.PokemonEntity

interface PokemonDetailRepository {
    suspend fun getEvolutionChainForPokemon(name: String): Result<List<PokemonEntity>>
    suspend fun getPokemonByName(name: String): Result<PokemonEntity>
}