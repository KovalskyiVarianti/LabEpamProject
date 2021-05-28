package com.example.labepamproject.data

import com.example.labepamproject.data.database.PokemonDatabase
import com.example.labepamproject.data.database.asDatabaseEntity
import com.example.labepamproject.data.database.asEntity
import com.example.labepamproject.data.network.PokedexApiService
import com.example.labepamproject.data.network.dto.PokemonPartialResponse
import com.example.labepamproject.data.network.dto.asEntity
import com.example.labepamproject.domain.GenerationEntity
import com.example.labepamproject.domain.PokemonEntity
import com.example.labepamproject.domain.PokemonRepository
import com.example.labepamproject.domain.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class NetworkRoomPokemonRepository(
    private val api: PokedexApiService,
    private val database: PokemonDatabase,
) : PokemonRepository {

    override suspend fun getPokemonByName(name: String): Result<PokemonEntity> =
        withContext(Dispatchers.IO) {
            try {
                Result.Success(
                    try {
                        database.pokemonDao.getPokemonByName(name).asEntity()
                    } catch (e: Exception) {
                        Timber.d(e)
                        api.fetchPokemonInfo(name).asEntity().cacheAndReturn()
                    }
                )
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getGenerations(): Result<List<GenerationEntity>> =
        withContext(Dispatchers.IO) {
            try {
                Result.Success(api.fetchGenerationList().results.map { it.asEntity() })
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
                    try {
                        getCachedPokemons(limit, offset)
                    } catch (e: Exception) {
                        Timber.d(e)
                        api.fetchPokemonList(limit, offset).results
                            .map { api.fetchPokemonInfo(it.name).asEntity() }
                            .cacheAndReturn()
                    }
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
                    .getForPageFromNetwork(offset, offset + limit)
                    .map { name ->
                        api.fetchPokemonInfo(name).asEntity()
                    }
            )
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private fun List<PokemonPartialResponse>.getForPageFromNetwork(fromIndex: Int, toIndex: Int) =
        when {
            fromIndex > size -> {
                Timber.d("empty")
                emptyList()
            }
            toIndex > size -> subList(fromIndex, size - 1).map { it.name }.also { Timber.d("to >") }
            else -> subList(fromIndex, toIndex).map { it.name }.also { Timber.d("else >") }
        }

    private fun getCachedPokemons(limit: Int, offset: Int) =
        database.pokemonDao.getPokemons().subList(offset, offset + limit).map { it.asEntity() }

    private fun List<PokemonEntity>.cacheAndReturn(): List<PokemonEntity> {
        database.pokemonDao.insertAll(*this.map { it.asDatabaseEntity() }.toTypedArray())
        return this
    }

    private fun PokemonEntity.cacheAndReturn(): PokemonEntity {
        database.pokemonDao.insertAll(this.asDatabaseEntity())
        return this
    }
}