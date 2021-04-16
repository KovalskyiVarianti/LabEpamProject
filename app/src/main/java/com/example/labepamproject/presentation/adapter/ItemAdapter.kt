package com.example.labepamproject.presentation.adapter

import android.graphics.Color
import com.example.labepamproject.databinding.ItemHeaderBinding
import com.example.labepamproject.databinding.ItemPokemonBinding
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.squareup.picasso.Picasso
import kotlin.random.Random

class ItemAdapter(pokemonClickListener: (Item.PokemonItem) -> Unit) :
    ListDelegationAdapter<List<Item>>() {
    init {
        delegatesManager.addDelegate(headerAdapterDelegate())
        delegatesManager.addDelegate(pokemonAdapterDelegate(pokemonClickListener))
    }

    private fun headerAdapterDelegate() =
        adapterDelegateViewBinding<Item.HeaderItem, Item, ItemHeaderBinding>(
            { layoutInflater, parent -> ItemHeaderBinding.inflate(layoutInflater, parent, false) }
        ) {
            bind {
                binding.headerText.text = item.text
            }

        }

    private fun pokemonAdapterDelegate(pokemonClickListener: (Item.PokemonItem) -> Unit) =
        adapterDelegateViewBinding<Item.PokemonItem, Item, ItemPokemonBinding>(
            { layoutInflater, parent -> ItemPokemonBinding.inflate(layoutInflater, parent, false) }
        ) {
            val rnd = Random
            binding.root.setOnClickListener {
                binding.root.setCardBackgroundColor(
                    Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
                )
                pokemonClickListener(item)
            }
            bind {
                binding.root.setCardBackgroundColor(
                    Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
                )
                binding.pokemonName.text = item.name
                Picasso.get().load(item.imgSrc).into(binding.pokemonImage)
            }
        }
}