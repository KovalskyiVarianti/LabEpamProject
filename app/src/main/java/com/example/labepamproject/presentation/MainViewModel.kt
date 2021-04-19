package com.example.labepamproject.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.labepamproject.R
import com.example.labepamproject.data.NetworkPokemonRepository
import com.example.labepamproject.data.network.createPokedexApiService
import com.example.labepamproject.domain.Pokemon
import com.example.labepamproject.domain.PokemonRepository
import com.example.labepamproject.presentation.adapter.Item
import com.example.labepamproject.presentation.adapter.Item.PokemonItem.Companion.asItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber


class MainViewModel(
    private val repository: PokemonRepository = NetworkPokemonRepository(
        createPokedexApiService
    )
) :
    ViewModel() {

    private lateinit var disposable: Disposable

    private val _pokemonListLiveData = MutableLiveData<List<Item>>()
    fun getPokemonList(): LiveData<List<Item>> = _pokemonListLiveData

    private val _state = MutableLiveData<MainViewState>()
    fun getState(): LiveData<MainViewState> = _state

    init {
        loadItems()
    }

    private fun loadItems() {
        _state.value = MainViewState.LoadingState(R.drawable.loading_animation)
        disposable = repository.getPokemons()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { pokemonList ->
                    val content = provideContent(pokemonList)
                    Timber.i("Values provided")
                    _state.value = MainViewState.ResultState(content)
                    Timber.i("Items loading is successful")
                },
                {
                    _state.value = MainViewState.ErrorState(R.drawable.ic_connection_error)
                }
            )
    }

    private fun provideContent(pokemons: List<Pokemon>) : List<Item>{
        val itemList = mutableListOf<Item>()
        itemList.add(Item.HeaderItem("Pokemons"))
        itemList.addAll(pokemons.map { it.asItem() })
        return itemList
    }

    override fun onCleared() {
        _pokemonListLiveData.value = null
    }
}