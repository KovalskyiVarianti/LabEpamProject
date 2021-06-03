package com.example.labepamproject.presentation.overview

import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.labepamproject.domain.PokemonEntity
import com.example.labepamproject.domain.PokemonRepository
import com.example.labepamproject.domain.Result
import com.example.labepamproject.presentation.overview.adapter.Item
import com.example.labepamproject.presentation.overview.adapter.asItem
import com.example.labepamproject.presentation.resolveError
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class PokemonOverviewViewModel(
    private val repository: PokemonRepository
) :
    ViewModel() {

    private var currentOffset: Int = 0
    private var generationData: List<Item.GenerationItem> = listOf()
    private var pokemonData: List<Item.PokemonItem> = listOf()
    private var currentFilter: PokemonFilter = PokemonFilter.AllPokemonFilter

    private val _headerText = MutableLiveData(DEFAULT_HEADER_TEXT)
    fun getHeaderText(): LiveData<String> = _headerText

    private val _state = MutableLiveData<PokemonOverviewViewState>()
    fun getState(): LiveData<PokemonOverviewViewState> = _state

    private val _navigateToPokemonDetailFragment =
        MutableLiveData<Triple<ImageView, String, Int>?>()

    fun navigateToPokemonDetailFragment(): MutableLiveData<Triple<ImageView, String, Int>?> =
        _navigateToPokemonDetailFragment

    fun onPokemonItemClicked(imageView: ImageView, name: String, color: Int) {
        _navigateToPokemonDetailFragment.value = Triple(imageView, name, color)
    }

    fun onPokemonDetailFragmentNavigated() {
        _navigateToPokemonDetailFragment.value = null
    }

    fun fetch() {
        if (pokemonData.isNotEmpty() && generationData.isNotEmpty()) {
            viewModelScope.launch {
                onLoadState()
                onGenerationResultState(generationData)
                delay(1)
                onPokemonResultState(pokemonData)
                onLoadingFinishedState()
            }
        } else {
            viewModelScope.launch {
                loadGenerations()
                delay(1)
                loadPokemons()
            }
        }

    }

    fun loadNextPokemons(offset: Int) {
        if (_state.value == PokemonOverviewViewState.LoadingState) {
            return
        }
        viewModelScope.launch {
            currentOffset = offset
            Timber.d("current offset = $currentOffset")
            loadPokemons(offset = currentOffset)
        }
    }

    fun onGenerationItemClicked(id: Int, generationName: String) =
        viewModelScope.launch {
            checkFilters(id)
            currentOffset = 0
            pokemonData = emptyList()
            loadPokemons()
            _headerText.value = generationName
        }


    private fun checkFilters(id: Int) {
        currentFilter = if (id in 1..8) {
            PokemonFilter.GenerationPokemonFilter(id)
        } else PokemonFilter.AllPokemonFilter
    }

    private suspend fun loadGenerations() {
        onLoadState()
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
        onLoadingFinishedState()
    }

    private suspend fun loadPokemons(limit: Int = ITEMS_PER_PAGE, offset: Int = 0) {
        onLoadState()
        when (val result: Result<List<PokemonEntity>> = loadPokemonByFilter(limit, offset)) {
            is Result.Success -> {
                currentOffset += offset
                val pokemon = result.data.map { it.asItem() }
                onPokemonResultState(pokemon)
            }
            is Result.Error -> {
                Timber.e(result.exception)
                onErrorState(result.exception)
            }
        }
        onLoadingFinishedState()
    }

    private suspend fun loadPokemonByFilter(limit: Int, offset: Int) = when (currentFilter) {
        is PokemonFilter.AllPokemonFilter -> repository.getPokemons(limit, offset)
        is PokemonFilter.GenerationPokemonFilter -> repository.getPokemonsByGeneration(
            (currentFilter as PokemonFilter.GenerationPokemonFilter).id,
            limit,
            offset
        )
    }

    private fun List<Item.GenerationItem>.provideGenerationDefaultItem(text: String) =
        listOf(Item.GenerationItem(0, text, true)) + this

    private fun onGenerationResultState(itemList: List<Item.GenerationItem>) {
        if (generationData.isEmpty()) {
            generationData = itemList
        }
        _state.postValue(PokemonOverviewViewState.GenerationResultState(generationData))
        Timber.d("Generation loading is successful")
        Timber.d("Current state: ${_state.value}")
    }

    private fun onPokemonResultState(itemList: List<Item.PokemonItem>) {
        if (!pokemonData.containsAll(itemList).also { Timber.d("Is new items: $it") }) {
            pokemonData = (pokemonData + itemList).distinct()
        }
        _state.postValue(PokemonOverviewViewState.PokemonResultState(pokemonData))
        Timber.d("Pokemon loading is successful")
        Timber.d("Current state: ${_state.value}")

    }

    private fun onLoadState() {
        _state.postValue(PokemonOverviewViewState.LoadingState)
        Timber.d("Current state: ${_state.value}")
    }

    private fun onErrorState(error: Throwable) {
        _state.value =
            PokemonOverviewViewState.ErrorState(resolveError(error))
        Timber.d("Current state: ${_state.value}")
        Timber.e(error)
    }

    private fun onLoadingFinishedState() {
        _state.value = PokemonOverviewViewState.LoadingFinishedState
        Timber.d("Current state: ${_state.value}")
    }
}