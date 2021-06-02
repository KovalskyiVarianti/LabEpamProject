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
                        database.pokemonDetailDao.getPokemonByName(name).asEntity()
                    } catch (e: Exception) {
                        val pokemon = api.fetchPokemonInfo(name).asEntity()
                        launch { database.pokemonDetailDao.insertAll(pokemon.asDatabaseEntity()) }
                        pokemon
                    }
                )
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getGenerations(): Result<List<GenerationEntity>> =
        withContext(Dispatchers.IO) {
            try {
                Result.Success(
                    try {
                        val generations =
                            database.generationDao.getGenerations().map { it.asEntity() }
                                .also { Timber.d("hello from gen database") }
                        if (generations.isEmpty()) throw Exception()
                        generations
                    } catch (e: Exception) {
                        api.fetchGenerationList().results.map {
                            val generation =
                                api.fetchGenerationInfo(getGenerationId(it.name)).asEntity()
                            launch { database.generationDao.insertAll(generation.asDatabaseEntity()) }
                            generation.also { Timber.d("hello from gen network") }
                        }
                    }
                )
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
                        database.pokemonDao.getPokemons().subList(offset, offset + limit)
                            .map { database.pokemonDetailDao.getPokemonByName(it.name).asEntity() }
                    } catch (e: Exception) {
                        Timber.d(e)
                        api.fetchPokemonList(limit, offset).results.map {
                            launch { database.pokemonDao.insertAll(PokemonDatabaseEntity(it.name)) }
                            val pokemon = api.fetchPokemonInfo(it.name).asEntity()
                            launch { database.pokemonDetailDao.insertAll(pokemon.asDatabaseEntity()) }
                            pokemon
                        }
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
                try {
                    database.generationDao.getGenerationById(generationId).pokemons
                        .subList(offset, offset + limit)
                        .map { name ->
                            database.pokemonDetailDao.getPokemonByName(name).asEntity()
                        }
                } catch (e: Exception) {
                    api.fetchGenerationInfo(generationId).pokemons
                        .getForPageFromNetwork(offset, offset + limit)
                        .mapNotNull { name ->
                            try {
                                val pokemon = api.fetchPokemonInfo(name).asEntity()
                                database.pokemonDetailDao.insertAll(pokemon.asDatabaseEntity())
                                pokemon
                            } catch (e: Exception) {
                                null
                            }
                        }
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
            toIndex > size -> subList(fromIndex, size).map { it.name }.also { Timber.d("to >") }
            else -> subList(fromIndex, toIndex).map { it.name }.also { Timber.d("else >") }
        }

}


