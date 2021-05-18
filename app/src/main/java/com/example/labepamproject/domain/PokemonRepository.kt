package com.example.labepamproject.domain

interface PokemonRepository {
    suspend fun getPokemons(): Result<List<PokemonEntity>>
    suspend fun getPokemonByName(name: String): Result<PokemonEntity>
    suspend fun getGenerations(): Result<List<GenerationEntity>>
}