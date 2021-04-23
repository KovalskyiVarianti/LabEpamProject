package com.example.labepamproject.presentation.overview

import com.example.labepamproject.presentation.overview.adapter.Item

sealed class PokemonOverviewViewState {
    object LoadingState : PokemonOverviewViewState()
    data class ErrorState(val errorMessage: String) : PokemonOverviewViewState()
    data class ResultState(val items: List<Item>) : PokemonOverviewViewState()
}