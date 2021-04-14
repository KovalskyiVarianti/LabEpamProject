package com.example.labepamproject.presentation.adapter

import com.example.labepamproject.databinding.ItemHeaderBinding
import com.example.labepamproject.databinding.ItemPokemonBinding
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.squareup.picasso.Picasso
import timber.log.Timber

class ItemAdapter : ListDelegationAdapter<List<Item>>() {
    init {
        delegatesManager.addDelegate(headerAdapterDelegate())
        delegatesManager.addDelegate(pokemonAdapterDelegate())
    }

    private fun headerAdapterDelegate() =
        adapterDelegateViewBinding<Item.HeaderItem, Item, ItemHeaderBinding>(
            { layoutInflater, parent -> ItemHeaderBinding.inflate(layoutInflater, parent, false) }
        ) {
            bind {
                Timber.i(it.toString())
                binding.headerText.text = item.text
            }

        }

    private fun pokemonAdapterDelegate() =
        adapterDelegateViewBinding<Item.PokemonItem, Item, ItemPokemonBinding>(
            { layoutInflater, parent -> ItemPokemonBinding.inflate(layoutInflater, parent, false) }
        ) {
            bind {
                binding.pokemonItem.text = item.name
                Picasso.get().load(item.imgSrc).into(binding.pokemonImage)
            }
        }
}