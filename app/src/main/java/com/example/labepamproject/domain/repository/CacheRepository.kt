package com.example.labepamproject.domain.repository

import com.example.labepamproject.data.database.PokemonDatabase
import com.example.labepamproject.data.database.PokemonDatabaseEntity
import com.example.labepamproject.data.database.asDatabaseEntity
import com.example.labepamproject.data.database.asEntity
import com.example.labepamproject.data.network.PokedexApiService
import com.example.labepamproject.data.network.dto.asEntity
import com.example.labepamproject.presentation.getGenerationId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

abstract class CacheRepository(
    private val api: PokedexApiService,
    private val database: PokemonDatabase,
) {
    protected suspend fun getPokemonNamesListFromCacheElseApi(limit: Int, offset: Int) = try {
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

    protected suspend fun getPokemonFromCacheElseApi(name: String) =
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

    protected suspend fun getGenerationsFromCacheElseApi() = try {
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
}