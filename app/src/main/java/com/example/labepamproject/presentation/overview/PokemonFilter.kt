package com.example.labepamproject.presentation.overview

sealed class PokemonFilter {
    object AllPokemonFilter : PokemonFilter()
    class GenerationPokemonFilter(val id: Int) : PokemonFilter()
}
