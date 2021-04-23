package com.example.labepamproject.presentation.detail

import com.example.labepamproject.domain.Pokemon

sealed class PokemonDetailViewState {
    object LoadingState : PokemonDetailViewState()
    data class ErrorState(val errorMessage: String) : PokemonDetailViewState()
    data class ResultState(val pokemon: Pokemon) : PokemonDetailViewState()
}