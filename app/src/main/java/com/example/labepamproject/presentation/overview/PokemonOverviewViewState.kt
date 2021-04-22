package com.example.labepamproject.presentation.overview

import com.example.labepamproject.presentation.overview.adapter.Item

sealed class PokemonOverviewViewState {
    data class LoadingState(val loadingImageId: Int) : PokemonOverviewViewState()
    data class ErrorState(val errorImageId: Int) : PokemonOverviewViewState()
    data class ResultState(val items: List<Item>) : PokemonOverviewViewState()
}