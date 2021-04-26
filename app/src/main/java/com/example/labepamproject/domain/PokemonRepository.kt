package com.example.labepamproject.domain

import io.reactivex.Single

interface PokemonRepository {
    fun getPokemons(): Single<List<PokemonEntity>>
    fun getPokemonByName(name: String): Single<PokemonEntity>
    fun getGenerations(): Single<List<GenerationEntity>>
}