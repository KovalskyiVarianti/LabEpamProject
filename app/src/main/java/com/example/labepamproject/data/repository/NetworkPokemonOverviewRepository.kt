package com.example.labepamproject.data.repository

import com.example.labepamproject.data.network.PokedexApiService
import com.example.labepamproject.data.network.dto.PokemonPartialResponse
import com.example.labepamproject.data.network.dto.asEntity
import com.example.labepamproject.domain.Result
import com.example.labepamproject.domain.entity.GenerationEntity
import com.example.labepamproject.domain.entity.PokemonEntity
import com.example.labepamproject.domain.repository.PokemonOverviewRepository
import com.example.labepamproject.presentation.getGenerationId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NetworkPokemonOverviewRepository(private val api: PokedexApiService) :
    PokemonOverviewRepository {

    override suspend fun getGenerations(): Result<List<GenerationEntity>> =
        withContext(Dispatchers.IO) {
            try {
                Result.Success(api.fetchGenerationList().results.map {
                    api.fetchGenerationInfo(getGenerationId(it.name)).asEntity()
                })
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getPokemons(
        limit: Int,
        offset: Int
    ): Result<List<PokemonEntity>> =
        withContext(Dispatchers.IO) {
            try {
                Result.Success(
                    api.fetchPokemonList(limit, offset).results
                        .map { api.fetchPokemonInfo(it.name).asEntity() }
                )
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getPokemonsByGeneration(
        generationId: Int,
        limit: Int,
        offset: Int
    ): Result<List<PokemonEntity>> = withContext(Dispatchers.IO) {
        try {
            Result.Success(
                api.fetchGenerationInfo(generationId).pokemons
                    .getForPage(offset, offset + limit)
                    .map { name ->
                        api.fetchPokemonInfo(name).asEntity()
                    }
            )
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private fun List<PokemonPartialResponse>.getForPage(fromIndex: Int, toIndex: Int) =
        when {
            fromIndex > size -> emptyList()
            toIndex > size -> subList(fromIndex, size - 1).map { it.name }
            else -> subList(fromIndex, toIndex).map { it.name }
        }
}