package com.example.labepamproject.data

import com.example.labepamproject.domain.Pokemon
import com.example.labepamproject.domain.PokemonRepository

class MockPokemonRepository : PokemonRepository {

    private val pokemonList = listOf(
        Pokemon("1","some1", generateUrlFromId(1)),
        Pokemon("2","some2", generateUrlFromId(2)),
        Pokemon("3","some3", generateUrlFromId(3)),
        Pokemon("4","some3", generateUrlFromId(4)),
        Pokemon("5","some3", generateUrlFromId(5)),
        Pokemon("6","some3", generateUrlFromId(6)),
        Pokemon("7","some3", generateUrlFromId(7)),
        Pokemon("8","some3", generateUrlFromId(8)),
        Pokemon("9","some3", generateUrlFromId(9)),
        Pokemon("10","some3", generateUrlFromId(10)),
        Pokemon("11","some3", generateUrlFromId(11)),
        Pokemon("12","some3", generateUrlFromId(12)),
        Pokemon("13","some3", generateUrlFromId(13)),
        Pokemon("14","some3", generateUrlFromId(14)),
        Pokemon("15","some3", generateUrlFromId(15)),
    )

    override fun getPokemons(): List<Pokemon> = pokemonList

    private fun generateUrlFromId(id: Int): String = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
}