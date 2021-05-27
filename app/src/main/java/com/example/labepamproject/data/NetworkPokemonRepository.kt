package com.example.labepamproject.data

import com.example.labepamproject.data.network.PokedexApiService
import com.example.labepamproject.data.network.dto.PokemonPartialResponse
import com.example.labepamproject.data.network.dto.toEntity
import com.example.labepamproject.domain.GenerationEntity
import com.example.labepamproject.domain.PokemonEntity
import com.example.labepamproject.domain.PokemonRepository
import com.example.labepamproject.domain.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class NetworkPokemonRepository(private val api: PokedexApiService) : PokemonRepository {

    override suspend fun getPokemonByName(name: String): Result<PokemonEntity> =
        withContext(Dispatchers.IO) {
            try {
                val pokemon = api.fetchPokemonInfo(name.toLowerCase(Locale.ROOT)).toEntity()
                Result.Success(pokemon)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getGenerations(): Result<List<GenerationEntity>> =
        withContext(Dispatchers.IO) {
            try {
                val generationList = api.fetchGenerationList().results.map { it.toEntity() }
                Result.Success(generationList)
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
                val pokemonNameList = api.fetchPokemonList(limit, offset).results.map { it.name }
                val pokemonDetailList = pokemonNameList.map { name ->
                    api.fetchPokemonInfo(name).toEntity()
                }
                Result.Success(pokemonDetailList)
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
            val pokemonNameList =
                api.fetchGenerationInfo(generationId).pokemons.getForPage(offset, offset + limit)
            val pokemonDetailList = pokemonNameList.map { name ->
                api.fetchPokemonInfo(name).toEntity()
            }
            Result.Success(pokemonDetailList)
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