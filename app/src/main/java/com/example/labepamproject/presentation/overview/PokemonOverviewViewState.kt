package com.example.labepamproject.presentation.overview

import com.example.labepamproject.presentation.overview.adapter.Item

sealed class PokemonOverviewViewState {
    object LoadingState : PokemonOverviewViewState()
    data class ErrorState(val errorMessage: String) : PokemonOverviewViewState()
    data class PokemonResultState(val pokemonItems: List<Item.PokemonItem>) :
        PokemonOverviewViewState()

    data class GenerationResultState(val generationItems: List<Item.GenerationItem>) :
        PokemonOverviewViewState()

    object LoadingFinishedState : PokemonOverviewViewState()
}