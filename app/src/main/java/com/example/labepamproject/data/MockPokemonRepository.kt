package com.example.labepamproject.data

import com.example.labepamproject.domain.Pokemon
import com.example.labepamproject.domain.PokemonRepository

class MockPokemonRepository : PokemonRepository {

    private val pokemonList = listOf(
        Pokemon("1","some1", "i"),
        Pokemon("2","some2", "i"),
        Pokemon("3","some3", "i"),
    )

    override fun getPokemons(): List<Pokemon> = pokemonList


}