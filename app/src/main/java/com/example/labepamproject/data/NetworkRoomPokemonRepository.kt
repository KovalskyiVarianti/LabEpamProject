package com.example.labepamproject.data

import com.example.labepamproject.data.database.PokemonDatabase
import com.example.labepamproject.data.database.PokemonDatabaseEntity
import com.example.labepamproject.data.database.asDatabaseEntity
import com.example.labepamproject.data.database.asEntity
import com.example.labepamproject.data.network.PokedexApiService
import com.example.labepamproject.data.network.dto.PokemonPartialResponse
import com.example.labepamproject.data.network.dto.asEntity
import com.example.labepamproject.domain.GenerationEntity
import com.example.labepamproject.domain.PokemonEntity
import com.example.labepamproject.domain.PokemonRepository
import com.example.labepamproject.domain.Result
import com.example.labepamproject.presentation.getGenerationId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber

class NetworkRoomPokemonRepository(
    private val api: PokedexApiService,
    private val database: PokemonDatabase,
) : PokemonRepository {

    override suspend fun getPokemonByName(name: String): Result<PokemonEntity> =
        withContext(Dispatchers.IO) {
            try {
                Result.Success(getPokemonFromCacheElseApi(name)!!)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getGenerations(): Result<List<GenerationEntity>> =
        withContext(Dispatchers.IO) {
            try {
                Result.Success(getGenerationsFromCacheElseApi())
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
                    getPokemonNamesListFromCacheElseApi(
                        limit,
                        offset
                    ).mapNotNull { getPokemonFromCacheElseApi(it) })
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
                database.generationDao.getGenerationById(generationId).pokemons
                    .subList(offset, offset + limit)
                    .mapNotNull { name ->
                        getPokemonFromCacheElseApi(name)
                    }
            )
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private suspend fun getPokemonNamesListFromCacheElseApi(limit: Int, offset: Int) = try {
        database.pokemonDao.getPokemons().subList(offset, offset + limit).map { it.name }
    } catch (e: Exception) {
        val pokemonNames = api.fetchPokemonList(limit, offset).results.map {
            withContext(Dispatchers.IO) {
                launch { database.pokemonDao.insertAll(PokemonDatabaseEntity(it.name)) }
            }
            it.name
        }

        pokemonNames
    }

    private suspend fun getPokemonFromCacheElseApi(name: String) =
        try {
            database.pokemonDetailDao.getPokemonByName(name).asEntity()
        } catch (e: Exception) {
            try {
                val pokemon = api.fetchPokemonInfo(name).asEntity()
                withContext(Dispatchers.IO) {
                    launch { database.pokemonDetailDao.insertAll(pokemon.asDatabaseEntity()) }
                }
                pokemon
            } catch (e: HttpException) {
                null
            }
        }

    private suspend fun getGenerationsFromCacheElseApi() = try {
        val generations = database.generationDao.getGenerations()
        if (generations.isEmpty()) throw IllegalStateException()
        generations.map { it.asEntity() }
    } catch (e: Exception) {
        api.fetchGenerationList().results.map {
            val generation = api.fetchGenerationInfo(getGenerationId(it.name)).asEntity()
            withContext(Dispatchers.IO) {
                launch {
                    database.generationDao.insertAll(generation.asDatabaseEntity())
                }
            }
            generation
        }
    }

    private fun List<PokemonPartialResponse>.getForPage(fromIndex: Int, toIndex: Int) =
        when {
            fromIndex > size -> {
                Timber.d("empty")
                emptyList()
            }
            toIndex > size -> subList(fromIndex, size).also { Timber.d("to >") }
            else -> subList(fromIndex, toIndex).also { Timber.d("else >") }
        }

}


