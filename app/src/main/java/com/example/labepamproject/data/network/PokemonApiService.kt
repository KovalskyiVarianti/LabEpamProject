package com.example.labepamproject.data.network

import com.example.labepamproject.data.network.dto.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val POKE_API_URL = "https://pokeapi.co/api/v2/"

interface PokedexApiService {

    @GET("pokemon")
    suspend fun fetchPokemonList(
        @Query("limit") limit: Int = 24,
        @Query("offset") offset: Int = 0,
    ): PokemonListResponse

    @GET("pokemon/{name}")
    suspend fun fetchPokemonInfo(@Path("name") name: String): PokemonDetailedResponse

    @GET("generation")
    suspend fun fetchGenerationList(): GenerationListResponse

    @GET("generation/{id}")
    suspend fun fetchGenerationInfo(@Path("id") id: Int): GenerationDetailedResponse

    @GET("pokemon-species/{name}")
    suspend fun fetchPokemonSpecies(@Path("name") name: String): PokemonSpeciesResponse

    @GET("evolution-chain/{id}")
    suspend fun fetchEvolutionChain(@Path("id") id: Int): EvolutionChainResponse
}