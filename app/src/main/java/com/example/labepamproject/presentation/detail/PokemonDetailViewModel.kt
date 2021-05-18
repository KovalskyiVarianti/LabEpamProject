package com.example.labepamproject.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labepamproject.data.NetworkPokemonRepository
import com.example.labepamproject.data.network.createPokedexApiService
import com.example.labepamproject.domain.PokemonEntity
import com.example.labepamproject.domain.PokemonRepository
import com.example.labepamproject.domain.Result
import kotlinx.coroutines.launch
import timber.log.Timber

class PokemonDetailViewModel(
    private val pokemonName: String,
    private val repository: PokemonRepository = NetworkPokemonRepository(
        createPokedexApiService
    )
) : ViewModel() {
    private val _state = MutableLiveData<PokemonDetailViewState>()
    fun getState(): LiveData<PokemonDetailViewState> = _state

    init {
        loadDetailedData()
    }

    private fun loadDetailedData() {
        onLoadState()
        viewModelScope.launch {
            when (val result = repository.getPokemonByName(pokemonName)) {
                is Result.Success -> {
                    onResultState(result.data)
                }
                is Result.Error -> {
                    Timber.e(result.exception)
                    onErrorState(result.exception)
                }
            }
        }
    }

    private fun onResultState(pokemon: PokemonEntity) {
        _state.value = (PokemonDetailViewState.ResultState(pokemon))
        Timber.d("Items loading is successful")
        Timber.d("Current state: ${_state.value}")
    }

    private fun onLoadState() {
        _state.value = PokemonDetailViewState.LoadingState
        Timber.d("Current state: ${_state.value}")
    }

    private fun onErrorState(error: Throwable) {
        _state.value =
            PokemonDetailViewState.ErrorState("$error")
        Timber.d("Current state: ${_state.value}")
        Timber.e(error)
    }
}