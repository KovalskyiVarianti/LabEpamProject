package com.example.labepamproject.presentation.overview.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.labepamproject.databinding.ItemGenerationListOverviewBinding
import com.example.labepamproject.databinding.ItemHeaderOverviewBinding
import com.example.labepamproject.databinding.ItemPokemonOverviewBinding
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import com.squareup.picasso.Picasso
import timber.log.Timber

class ItemAdapter(
    private val defaultGenerationItem: Item.GenerationItem,
    pokemonClickListener: (Item.PokemonItem) -> Unit = {},
    generationClickListener: (Int) -> Unit = {},
) :
    AsyncListDifferDelegationAdapter<Item>(DiffCallback) {

    init {
        delegatesManager.addDelegate(headerAdapterDelegate())
        delegatesManager.addDelegate(pokemonAdapterDelegate(pokemonClickListener))
        delegatesManager.addDelegate(generationListAdapterDelegate(generationClickListener))
    }

    private fun generationListAdapterDelegate(generationClickListener: (Int) -> Unit) =
        adapterDelegateViewBinding<Item.GenerationListItem, Item, ItemGenerationListOverviewBinding>(
            { layoutInflater, parent ->
                ItemGenerationListOverviewBinding.inflate(
                    layoutInflater, parent, false
                )
            }
        ) {
            val generationAdapter = GenerationListAdapter(generationClickListener)
            bind {
                generationAdapter.items = adaptGenerationList(item.generationList)
                binding.generationOverviewList.adapter = generationAdapter
                Timber.d("GenerationList binded")
            }
        }

    private fun adaptGenerationList(generationItemList: List<Item.GenerationItem>) =
        listOf(defaultGenerationItem) + generationItemList

    private fun headerAdapterDelegate() =
        adapterDelegateViewBinding<Item.HeaderItem, Item, ItemHeaderOverviewBinding>(
            { layoutInflater, parent -> ItemHeaderOverviewBinding.inflate(layoutInflater, parent, false) }
        ) {
            bind {
                binding.headerOverviewText.text = item.text
                Timber.d("Header binded")
            }

        }

    private fun pokemonAdapterDelegate(pokemonClickListener: (Item.PokemonItem) -> Unit) =
        adapterDelegateViewBinding<Item.PokemonItem, Item, ItemPokemonOverviewBinding>(
            { layoutInflater, parent -> ItemPokemonOverviewBinding.inflate(layoutInflater, parent, false) }
        ) {
            binding.root.setOnClickListener {
                pokemonClickListener(item)
            }
            bind {
                binding.pokemonOverviewName.text = adaptPokemonName(item.name)
                binding.pokemonOverviewImage.contentDescription = item.name
                Picasso.get().load(item.imgSrc).into(binding.pokemonOverviewImage)
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