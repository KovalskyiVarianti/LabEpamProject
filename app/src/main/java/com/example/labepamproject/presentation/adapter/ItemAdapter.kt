package com.example.labepamproject.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.labepamproject.databinding.ItemGenerationListBinding
import com.example.labepamproject.databinding.ItemHeaderBinding
import com.example.labepamproject.databinding.ItemPokemonBinding
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.squareup.picasso.Picasso
import timber.log.Timber

class ItemAdapter(
    private val defaultGenerationItem: Item.GenerationItem,
    pokemonClickListener: (Item.PokemonItem) -> Unit = {}
) :
    AsyncListDifferDelegationAdapter<Item>(DiffCallback) {

    init {
        delegatesManager.addDelegate(headerAdapterDelegate())
        delegatesManager.addDelegate(pokemonAdapterDelegate(pokemonClickListener))
        delegatesManager.addDelegate(generationListAdapterDelegate())
    }

    private fun generationListAdapterDelegate() =
        adapterDelegateViewBinding<Item.GenerationListItem, Item, ItemGenerationListBinding>(
            { layoutInflater, parent ->
                ItemGenerationListBinding.inflate(
                    layoutInflater, parent, false
                )
            }
        ) {
            val generationAdapter = GenerationListAdapter()
            bind {
                generationAdapter.items = adaptGenerationList(item.generationList)
                binding.generationList.adapter = generationAdapter
                Timber.d("GenerationList binded")
            }
        }

    private fun adaptGenerationList(generationItemList: List<Item.GenerationItem>) =
        listOf(defaultGenerationItem) + generationItemList

    private fun headerAdapterDelegate() =
        adapterDelegateViewBinding<Item.HeaderItem, Item, ItemHeaderBinding>(
            { layoutInflater, parent -> ItemHeaderBinding.inflate(layoutInflater, parent, false) }
        ) {
            bind {
                binding.headerText.text = item.text
                Timber.d("Header binded")
            }

        }

    private fun pokemonAdapterDelegate(pokemonClickListener: (Item.PokemonItem) -> Unit) =
        adapterDelegateViewBinding<Item.PokemonItem, Item, ItemPokemonBinding>(
            { layoutInflater, parent -> ItemPokemonBinding.inflate(layoutInflater, parent, false) }
        ) {
            binding.root.setOnClickListener {
                pokemonClickListener(item)
            }
            bind {
                binding.pokemonName.text = adaptPokemonName(item.name)
                Picasso.get().load(item.imgSrc).into(binding.pokemonImage)
                Timber.d("Pokemon ${item.name} binded")
            }
        }

    private fun adaptPokemonName(name: String) =
        name.replaceFirst(name[0], name[0].toUpperCase())


    companion object DiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            Timber.d("itemsTheSame chech")
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            Timber.d("contentsTheSame check")
            return oldItem == newItem
        }
    }

}