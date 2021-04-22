package com.example.labepamproject.presentation.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.labepamproject.R
import com.example.labepamproject.data.NetworkPokemonRepository
import com.example.labepamproject.data.network.createPokedexApiService
import com.example.labepamproject.domain.PokemonRepository
import com.example.labepamproject.presentation.overview.adapter.Item
import com.example.labepamproject.presentation.overview.adapter.Item.GenerationItem.Companion.asItem
import com.example.labepamproject.presentation.overview.adapter.Item.PokemonItem.Companion.asItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber


class PokemonOverviewViewModel(
    private val repository: PokemonRepository = NetworkPokemonRepository(
        createPokedexApiService
    )
) :
    ViewModel() {

    private lateinit var disposable: Disposable
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
        _state.value = PokemonOverviewViewState.LoadingState(R.drawable.loading_animation)
        Timber.d("Current state: ${_state.value}")
        disposable = repository.getPokemons()
            .zipWith(repository.getGenerations(), { pokemons, generations ->
                listOf(Item.GenerationListItem(generations.map { it.asItem() })) + pokemons.map { it.asItem() }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { items ->
                    _state.postValue(PokemonOverviewViewState.ResultState(items))
                    Timber.d("Items loading is successful")
                    Timber.d("Current state: ${_state.value}")
                },
                {
                    _state.value =
                        PokemonOverviewViewState.ErrorState(R.drawable.ic_connection_error)
                    Timber.d("Current state: ${_state.value}")
                    Timber.e(it)
                }
            )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
        Timber.d("Resource is disposed")
    }
}