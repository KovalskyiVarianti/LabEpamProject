package com.example.labepamproject.domain

interface PokemonRepository {
    suspend fun getPokemons(limit: Int, offset: Int): Result<List<PokemonEntity>>
    suspend fun getPokemonsByGeneration(
        generationId: Int,
        limit: Int,
        offset: Int
    ): Result<List<PokemonEntity>>

    suspend fun getPokemonByName(name: String): Result<PokemonEntity>
    suspend fun getGenerations(): Result<List<GenerationEntity>>
}