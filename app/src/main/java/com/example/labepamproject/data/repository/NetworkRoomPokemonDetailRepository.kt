package com.example.labepamproject.data.repository

import com.example.labepamproject.data.database.PokemonDatabase
import com.example.labepamproject.data.network.PokedexApiService
import com.example.labepamproject.data.network.dto.ChainResponse
import com.example.labepamproject.domain.Result
import com.example.labepamproject.domain.entity.PokemonEntity
import com.example.labepamproject.domain.repository.CacheRepository
import com.example.labepamproject.domain.repository.PokemonDetailRepository
import com.example.labepamproject.presentation.getIdByUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NetworkRoomPokemonDetailRepository(
    private val api: PokedexApiService,
    private val database: PokemonDatabase,
) : CacheRepository(api, database), PokemonDetailRepository {

    override suspend fun getEvolutionChainForPokemon(name: String) = withContext(Dispatchers.IO) {
        try {
            val pokemonList = mutableListOf<PokemonEntity>()
            val evolutionChainId =
                getIdByUrl(api.fetchPokemonSpecies(name).evolutionChain.url).toInt()
            val chain: ChainResponse = api.fetchEvolutionChain(evolutionChainId).chain
            var chains: List<ChainResponse?>? = listOf(chain)
            while (!chains.isNullOrEmpty()) {
                pokemonList.add(getPokemonFromCacheElseApi(chains[0]!!.species.name)!!)
                chains = chains[0]?.nextChain
            }
            Result.Success(
                pokemonList
            )
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getPokemonByName(name: String): Result<PokemonEntity> =
        withContext(Dispatchers.IO) {
            try {
                Result.Success(getPokemonFromCacheElseApi(name)!!)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
}