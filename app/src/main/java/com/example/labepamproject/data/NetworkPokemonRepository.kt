package com.example.labepamproject.data

import com.example.labepamproject.data.network.GenerationPartialResponse
import com.example.labepamproject.data.network.PokedexApiService
import com.example.labepamproject.data.network.PokemonDetailedResponse
import com.example.labepamproject.domain.GenerationEntity
import com.example.labepamproject.domain.PokemonEntity
import com.example.labepamproject.domain.PokemonRepository
import com.example.labepamproject.domain.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NetworkPokemonRepository(val api: PokedexApiService) : PokemonRepository {
    override suspend fun getPokemons(): Result<List<PokemonEntity>> = withContext(Dispatchers.IO) {
        try {
            val pokemonNameList = api.fetchPokemonList().results.map { it.name }
            val pokemonDetailList = pokemonNameList.map { name ->
                api.fetchPokemonInfo(name).toEntity()
            }
            Result.Success(pokemonDetailList)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getPokemonByName(name: String): Result<PokemonEntity> =
        withContext(Dispatchers.IO) {
            try {
                val pokemon = api.fetchPokemonInfo(name).toEntity()
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

    private fun PokemonDetailedResponse.toEntity() =
        PokemonEntity(
            id = id,
            name = name,
            prevImgUrl = generateUrlFromId(id),
            experience = experience,
            height = height,
            weight = weight,
            abilities = abilities.map { it.ability.name },
            stats = stats.map { it.stat.name to it.base_stat },
            types = types.map { it.type.name }
        )

    private fun GenerationPartialResponse.toEntity() =
        GenerationEntity(name)

    private fun generateUrlFromId(id: Int): String =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
}