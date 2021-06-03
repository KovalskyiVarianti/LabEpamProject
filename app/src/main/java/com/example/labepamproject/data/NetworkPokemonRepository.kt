package com.example.labepamproject.data

import com.example.labepamproject.data.network.PokedexApiService
import com.example.labepamproject.data.network.dto.PokemonPartialResponse
import com.example.labepamproject.data.network.dto.asEntity
import com.example.labepamproject.domain.GenerationEntity
import com.example.labepamproject.domain.PokemonEntity
import com.example.labepamproject.domain.PokemonRepository
import com.example.labepamproject.domain.Result
import com.example.labepamproject.presentation.getGenerationId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NetworkPokemonRepository(private val api: PokedexApiService) : PokemonRepository {

    override suspend fun getPokemonByName(name: String): Result<PokemonEntity> =
        withContext(Dispatchers.IO) {
            try {
                Result.Success(api.fetchPokemonInfo(name).asEntity())
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

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

    override suspend fun getEvolutionChainForPokemon(name: String): Result<List<PokemonEntity>> {
        TODO("Not yet implemented")
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