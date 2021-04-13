package com.example.labepamproject.domain

interface PokemonRepository {
    fun getPokemons(): List<Pokemon>
}