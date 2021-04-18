package com.example.labepamproject.data.network

import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

const val POKE_API_URL = "https://pokeapi.co/api/v2/"

private val retrofit = Retrofit.Builder()
    .baseUrl(POKE_API_URL)
    .addConverterFactory(MoshiConverterFactory.create())
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .build()

fun createPokedexApiService() : PokedexApiService =
    retrofit.create(PokedexApiService::class.java)



interface PokedexApiService {
}