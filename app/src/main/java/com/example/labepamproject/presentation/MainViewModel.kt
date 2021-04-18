package com.example.labepamproject.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.labepamproject.data.MockPokemonRepository
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


class MainViewModel(private val repository: PokemonRepository = NetworkPokemonRepository(
    createPokedexApiService
)) :
    ViewModel() {

    private lateinit var disposable: Disposable

    private val _pokemonListLiveData = MutableLiveData<List<Item>>()
    fun getPokemonList(): LiveData<List<Item>> = _pokemonListLiveData

    fun loadItems() {
        disposable = repository.getPokemons()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    showItems(it)
                    Timber.i("Items loading is successful")
                },
                {
                    Timber.e(it)
                }
            )
    }

    private fun showItems(pokemons: List<Pokemon>) {
        val itemList = mutableListOf<Item>()
        itemList.add(Item.HeaderItem("Pokemons"))
        itemList.addAll(pokemons.map { it.asItem() })
        _pokemonListLiveData.postValue(itemList)
        Timber.i("Values posted")
    }

    override fun onCleared() {
        _pokemonListLiveData.value = null
    }
}