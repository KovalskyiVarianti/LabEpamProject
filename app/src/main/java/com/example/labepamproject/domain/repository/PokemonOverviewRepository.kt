package com.example.labepamproject.domain.repository

import com.example.labepamproject.domain.Result
import com.example.labepamproject.domain.entity.GenerationEntity
import com.example.labepamproject.domain.entity.PokemonEntity

interface PokemonOverviewRepository {
    suspend fun getPokemons(limit: Int, offset: Int): Result<List<PokemonEntity>>
    suspend fun getPokemonsByGeneration(
        generationId: Int,
        limit: Int,
        offset: Int
    ): Result<List<PokemonEntity>>


    suspend fun getGenerations(): Result<List<GenerationEntity>>

}