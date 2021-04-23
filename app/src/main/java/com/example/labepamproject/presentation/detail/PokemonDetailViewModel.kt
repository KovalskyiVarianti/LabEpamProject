package com.example.labepamproject.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.labepamproject.data.NetworkPokemonRepository
import com.example.labepamproject.data.network.createPokedexApiService
import com.example.labepamproject.domain.PokemonRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PokemonDetailViewModel(
    private val pokemonName: String,
    private val repository: PokemonRepository = NetworkPokemonRepository(
        createPokedexApiService
    )
) : ViewModel() {
    private lateinit var disposable: Disposable
    private val _state = MutableLiveData<PokemonDetailViewState>()
    fun getState(): LiveData<PokemonDetailViewState> = _state

    init {
        loadDetailedData()
    }

    private fun loadDetailedData() {
        _state.value = PokemonDetailViewState.LoadingState
        disposable = repository.getPokemonByName(pokemonName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ pokemon ->
                _state.value = PokemonDetailViewState.ResultState(pokemon)
            }, { error ->
                _state.value = PokemonDetailViewState.ErrorState("$error")
            })
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}