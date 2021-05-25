package com.example.labepamproject.presentation.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labepamproject.domain.PokemonRepository
import com.example.labepamproject.domain.Result
import com.example.labepamproject.presentation.overview.adapter.Item
import com.example.labepamproject.presentation.overview.adapter.Item.GenerationItem.Companion.asItem
import com.example.labepamproject.presentation.overview.adapter.Item.PokemonItem.Companion.asItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

const val DEFAULT_HEADER_TEXT = "ALL"
const val ITEMS_PER_PAGE: Int = 24
const val SPAN_COUNT_PORTRAIT: Int = 3
const val SPAN_COUNT_LANDSCAPE: Int = 6

class PokemonOverviewViewModel(
    private val repository: PokemonRepository
) :
    ViewModel() {
    private var currentOffset: Int = 0
    private var generationData: List<Item.GenerationItem> = listOf()
    private var pokemonData: List<Item.PokemonItem> = listOf()
    private var currentGenerationId: Int = 0

    private val _headerText = MutableLiveData(DEFAULT_HEADER_TEXT)
    fun getHeaderText(): LiveData<String> = _headerText

    private val _state = MutableLiveData<PokemonOverviewViewState>()
    fun getState(): LiveData<PokemonOverviewViewState> = _state

    private val _navigateToPokemonDetailFragment = MutableLiveData<Pair<String, Int>?>()
    fun navigateToPokemonDetailFragment(): MutableLiveData<Pair<String, Int>?> =
        _navigateToPokemonDetailFragment

    fun onPokemonItemClicked(name: String, color: Int) {
        _navigateToPokemonDetailFragment.value = name to color
    }

    fun onPokemonDetailFragmentNavigated() {
        _navigateToPokemonDetailFragment.value = null
    }

    fun fetch() {
        if (pokemonData.isNotEmpty() && generationData.isNotEmpty()) {
            viewModelScope.launch {
                onGenerationResultState(generationData)
                delay(1)
                onPokemonResultState(pokemonData)
            }
        } else {
            onLoadState()
            viewModelScope.launch {
                loadGenerations()
                loadPokemons()
                onLoadingFinishedState()
            }
        }

    }

    fun loadNextPokemons(offset: Int) {
        currentOffset += offset
        onLoadState()
        viewModelScope.launch {
            loadPokemons(offset = currentOffset)
            onLoadingFinishedState()
        }
    }

    fun onGenerationItemClicked(id: Int, generationName: String) {
        updateData(id, generationName)
    }

    private fun updateData(id: Int, generationName: String) {
        onLoadState()
        viewModelScope.launch {
            currentGenerationId = id
            currentOffset = 0
            pokemonData = emptyList()
            loadPokemons()
            _headerText.value = generationName
            onLoadingFinishedState()
        }
    }

    private suspend fun loadGenerations() {
        when (val result = repository.getGenerations()) {
            is Result.Success -> {
                val generationItems = result.data.map { it.asItem() }
                    .provideGenerationDefaultItem(DEFAULT_HEADER_TEXT)
                onGenerationResultState(generationItems)
            }
            is Result.Error -> {
                Timber.e(result.exception)
                onErrorState(result.exception)
            }
        }
    }

    private suspend fun loadPokemons(limit: Int = ITEMS_PER_PAGE, offset: Int = 0) {
        when (val result = repository.getPokemons(currentGenerationId, limit, offset)) {
            is Result.Success -> {
                val pokemonItem = result.data.map { it.asItem() }
                onPokemonResultState(pokemonItem)
            }
            is Result.Error -> {
                Timber.e(result.exception)
                onErrorState(result.exception)
            }
        }
    }

    private fun List<Item.GenerationItem>.provideGenerationDefaultItem(text: String) =
        listOf(Item.GenerationItem(0, text, true)) + this

    private fun onGenerationResultState(itemList: List<Item.GenerationItem>) {
        if (generationData.isEmpty()) {
            generationData = itemList
        }
        _state.value = PokemonOverviewViewState.GenerationResultState(generationData)
        Timber.d("Generation loading is successful")
        Timber.d("Current state: ${_state.value}")
    }

    private fun onPokemonResultState(itemList: List<Item.PokemonItem>) {
        if (!pokemonData.containsAll(itemList)) {
            pokemonData += itemList
        }
        _state.value = PokemonOverviewViewState.PokemonResultState(pokemonData)
        Timber.d("Pokemon loading is successful")
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