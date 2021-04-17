package com.example.labepamproject.data

import com.example.labepamproject.domain.Pokemon
import com.example.labepamproject.domain.PokemonRepository

class MockPokemonRepository : PokemonRepository {

    private val pokemonList = listOf(
        Pokemon("1","bigpokemonname", generateUrlFromId(1)),
        Pokemon("2","small", generateUrlFromId(2)),
        Pokemon("3","mediumname", generateUrlFromId(3)),
        Pokemon("4","4", generateUrlFromId(4)),
        Pokemon("5","5", generateUrlFromId(5)),
        Pokemon("6","6", generateUrlFromId(6)),
        Pokemon("7","7", generateUrlFromId(7)),
        Pokemon("8","8", generateUrlFromId(8)),
        Pokemon("9","9", generateUrlFromId(9)),
        Pokemon("10","10", generateUrlFromId(10)),
        Pokemon("11","11", generateUrlFromId(11)),
        Pokemon("12","12", generateUrlFromId(12)),
        Pokemon("13","12", generateUrlFromId(13)),
        Pokemon("14","14", generateUrlFromId(14)),
        Pokemon("15","15", generateUrlFromId(15)),
    )

    override fun getPokemons(): List<Pokemon> = pokemonList

    private fun generateUrlFromId(id: Int): String = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
}