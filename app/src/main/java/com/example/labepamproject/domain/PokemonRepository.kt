package com.example.labepamproject.domain

import io.reactivex.Single

interface PokemonRepository {
    fun getPokemons(): Single<List<Pokemon>>
    fun getPokemonByName(name: String) : Single<Pokemon>
    fun getGenerations() : Single<List<Generation>>
}