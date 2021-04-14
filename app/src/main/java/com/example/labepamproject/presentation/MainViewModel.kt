package com.example.labepamproject.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.labepamproject.data.MockPokemonRepository
import com.example.labepamproject.domain.PokemonRepository
import com.example.labepamproject.presentation.adapter.Item
import com.example.labepamproject.presentation.adapter.Item.PokemonItem.Companion.asItem


class MainViewModel(private val repository: PokemonRepository = MockPokemonRepository()) : ViewModel() {

    private val _pokemonListLiveData = MutableLiveData<List<Item>>()
    fun getPokemonList() :LiveData<List<Item>> = _pokemonListLiveData

    fun loadItems(){
        val itemList = mutableListOf<Item>()
        itemList.add(Item.HeaderItem("Header"))
        itemList.addAll(repository.getPokemons().map { it.asItem() })
        _pokemonListLiveData.value = itemList
    }

    override fun onCleared() {
        _pokemonListLiveData.value = null
    }
}