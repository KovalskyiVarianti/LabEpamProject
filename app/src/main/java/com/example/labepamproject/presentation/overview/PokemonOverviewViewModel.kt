package com.example.labepamproject.presentation.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labepamproject.data.NetworkPokemonRepository
import com.example.labepamproject.data.network.createPokedexApiService
import com.example.labepamproject.domain.PokemonRepository
import com.example.labepamproject.domain.Result
import com.example.labepamproject.presentation.overview.adapter.Item
import com.example.labepamproject.presentation.overview.adapter.Item.GenerationItem.Companion.asItem
import com.example.labepamproject.presentation.overview.adapter.Item.PokemonItem.Companion.asItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber


class PokemonOverviewViewModel(
    private val repository: PokemonRepository = NetworkPokemonRepository(
        createPokedexApiService
    )
) :
    ViewModel() {
    private var currentOffset: Int = 0
    private val data: MutableList<Item> = mutableListOf()

    fun loadData(list: List<Item>): List<Item> {
        data.addAll(list)
        return data.toList()
    }

    private val _state = MutableLiveData<PokemonOverviewViewState>()
    fun getState(): LiveData<PokemonOverviewViewState> = _state

    private val _navigateToPokemonDetailFragment = MutableLiveData<String>()
    fun navigateToPokemonDetailFragment(): LiveData<String> = _navigateToPokemonDetailFragment

    fun onPokemonItemClicked(name: String) {
        _navigateToPokemonDetailFragment.value = name
    }

    fun onPokemonDetailFragmentNavigated() {
        _navigateToPokemonDetailFragment.value = null
    }

    init {
        loadItems()
    }

    private fun loadItems() {
        onLoadState()
        viewModelScope.launch {
            loadGenerations()
            loadPokemons()
            onLoadingFinishedState()
        }
    }

    private suspend fun loadPokemons(limit: Int = 24, offset: Int = 0) {
        when (val result = repository.getPokemons(limit, offset)) {
            is Result.Success -> {
                val pokemonItem = result.data.map { it.asItem() }
                onResultState(pokemonItem)
            }
            is Result.Error -> {
                Timber.e(result.exception)
                onErrorState(result.exception)
            }
        }
    }

    fun loadNextPokemons() {
        currentOffset += 24
        onLoadState()
        viewModelScope.launch {
            loadPokemons(offset = currentOffset)
            onLoadingFinishedState()
        }
    }

    private suspend fun loadGenerations() {
        delay(2000)
        when (val result = repository.getGenerations()) {
            is Result.Success -> {
                val generationListItem = Item.GenerationListItem(result.data.map { it.asItem() })
                onResultState(listOf(generationListItem).provideHeader("Head"))
            }
            is Result.Error -> {
                Timber.e(result.exception)
                onErrorState(result.exception)
            }
        }
    }

    private fun List<Item>.provideHeader(text: String) =
        listOf(Item.HeaderItem(text)) + this

    private fun onResultState(items: List<Item>) {
        _state.value = (PokemonOverviewViewState.ResultState(items))
        Timber.d("Items loading is successful")
        Timber.d("Current state: ${_state.value}")
    }

    private fun onLoadState() {
        _state.value = PokemonOverviewViewState.LoadingState
        Timber.d("Current state: ${_state.value}")
    }

    private fun onErrorState(error: Throwable) {
        _state.value =
            PokemonOverviewViewState.ErrorState("$error")
        Timber.d("Current state: ${_state.value}")
        Timber.e(error)
    }

    private fun onLoadingFinishedState() {
        _state.value = PokemonOverviewViewState.LoadingFinishedState
        Timber.d("Current state: ${_state.value}")
    }
}