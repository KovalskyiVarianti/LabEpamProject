package com.example.labepamproject.presentation.detail

import com.example.labepamproject.domain.entity.PokemonEntity

sealed class PokemonDetailViewState {
    object LoadingState : PokemonDetailViewState()
    data class ErrorState(val errorMessage: String) : PokemonDetailViewState()
    data class ResultState(val pokemonEntity: PokemonEntity) : PokemonDetailViewState()
    data class ChainResultState(val pokemonEntities: List<PokemonEntity>) : PokemonDetailViewState()
}