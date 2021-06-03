package com.example.labepamproject.data.repository

import com.example.labepamproject.data.database.PokemonDatabase
import com.example.labepamproject.data.network.PokedexApiService
import com.example.labepamproject.domain.Result
import com.example.labepamproject.domain.entity.GenerationEntity
import com.example.labepamproject.domain.entity.PokemonEntity
import com.example.labepamproject.domain.repository.CacheRepository
import com.example.labepamproject.domain.repository.PokemonOverviewRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NetworkRoomPokemonOverviewRepository(
    private val api: PokedexApiService,
    private val database: PokemonDatabase,
) : CacheRepository(api, database), PokemonOverviewRepository {

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
}


