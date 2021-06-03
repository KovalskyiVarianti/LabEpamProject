package com.example.labepamproject.presentation.detail

import android.content.Intent
import androidx.core.app.ShareCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labepamproject.domain.Result
import com.example.labepamproject.domain.entity.PokemonEntity
import com.example.labepamproject.domain.repository.PokemonDetailRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class PokemonDetailViewModel(
    private val pokemonName: String,
    private val detailRepository: PokemonDetailRepository
) : ViewModel() {
    private val _state = MutableLiveData<PokemonDetailViewState>()
    fun getState(): LiveData<PokemonDetailViewState> = _state

    private val _navigateToPokemonWikiFragment =
        MutableLiveData<String?>()

    fun navigateToPokemonWikiFragment(): MutableLiveData<String?> =
        _navigateToPokemonWikiFragment

    fun onMoreButtonClicked(pokemonName: String) {
        _navigateToPokemonWikiFragment.value = pokemonName
    }

    fun onPokemonWikiFragmentNavigated() {
        _navigateToPokemonWikiFragment.value = null
    }

    fun fetch() {
        viewModelScope.launch {
            loadPokemon()
            loadChains()
        }
    }

    private fun loadPokemon() {
        onLoadState()
        viewModelScope.launch {
            when (val result = detailRepository.getPokemonByName(pokemonName)) {
                is Result.Success -> {
                    Timber.d("${result.data}")
                    onResultState(result.data)
                }
                is Result.Error -> {
                    Timber.e(result.exception)
                    onErrorState(result.exception)
                }
            }
        }
    }

    private fun loadChains() {
        onLoadState()
        viewModelScope.launch {
            when (val result = detailRepository.getEvolutionChainForPokemon(pokemonName)) {
                is Result.Success -> {
                    onChainResultState(result.data)
                }
                is Result.Error -> {
                    onErrorState(result.exception)
                }
            }
        }
    }

    fun buildShareIntent(intentBuilder: ShareCompat.IntentBuilder, text: String): Intent {
        return intentBuilder
            .setText(text)
            .setType("text/plain")
            .intent
    }

    private fun onResultState(pokemon: PokemonEntity) {
        _state.value = (PokemonDetailViewState.ResultState(pokemon))
        Timber.d("Items loading is successful")
        Timber.d("Current state: ${_state.value}")
    }

    private fun onChainResultState(pokemons: List<PokemonEntity>) {
        _state.value = PokemonDetailViewState.ChainResultState(pokemons)
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