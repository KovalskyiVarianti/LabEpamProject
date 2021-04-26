package com.example.labepamproject.data.network

import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val POKE_API_URL = "https://pokeapi.co/api/v2/"

private val retrofit = Retrofit.Builder()
    .baseUrl(POKE_API_URL)
    .addConverterFactory(MoshiConverterFactory.create())
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .build()

val createPokedexApiService: PokedexApiService by lazy {
    retrofit.create(PokedexApiService::class.java)
}


interface PokedexApiService {

    @GET("pokemon")
    fun fetchPokemonList(
        @Query("limit") limit: Int = 24,
        @Query("offset") offset: Int = 0,
    ): Single<PokemonListResponse>

    @GET("pokemon/{name}")
    fun fetchPokemonInfo(@Path("name") name: String): Single<PokemonDetailedResponse>

    @GET("generation")
    fun fetchGenerationList(): Single<GenerationListResponse>
}