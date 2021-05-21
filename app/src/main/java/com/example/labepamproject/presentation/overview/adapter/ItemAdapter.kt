package com.example.labepamproject.presentation.overview.adapter

import android.graphics.Color
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.example.labepamproject.R
import com.example.labepamproject.databinding.ItemGenerationListOverviewBinding
import com.example.labepamproject.databinding.ItemHeaderOverviewBinding
import com.example.labepamproject.databinding.ItemPokemonOverviewBinding
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import timber.log.Timber
import kotlin.random.Random

class ItemAdapter(
    pokemonClickListener: (String, Int) -> Unit,
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
                generationAdapter.items = item.generationList
                binding.generationOverviewList.adapter = generationAdapter
                Timber.d("GenerationList binded")
            }
        }

    private fun headerAdapterDelegate() =
        adapterDelegateViewBinding<Item.HeaderItem, Item, ItemHeaderOverviewBinding>(
            { layoutInflater, parent ->
                ItemHeaderOverviewBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            }
        ) {
            bind {
                binding.headerOverviewText.text = item.text
                Timber.d("Header binded")
            }

        }

    private fun pokemonAdapterDelegate(pokemonClickListener: (String, Int) -> Unit) =
        adapterDelegateViewBinding<Item.PokemonItem, Item, ItemPokemonOverviewBinding>(
            { layoutInflater, parent ->
                ItemPokemonOverviewBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            }
        ) {
            val color = Random.getRandomColor()
            binding.root.setOnClickListener {
                pokemonClickListener(item.name, color)
            }
            bind {

                binding.pokemonOverviewImage.setBackgroundColor(color)
                binding.pokemonOverviewName.text = adaptPokemonName(item.name)
                binding.pokemonOverviewImage.loadImage(item.imgSrc)
                Timber.d("Pokemon ${item.name} binded")
            }
        }

    private fun Random.getRandomColor() = Color.rgb(
        nextInt(256),
        nextInt(256),
        nextInt(256)
    )

    private fun ImageView.loadImage(url: String) {
        Glide.with(context)
            .load(url)
            .placeholder(R.drawable.loading_image_placeholder)
            .into(this)
    }

    private fun adaptPokemonName(name: String) = name.replaceFirst(name[0], name[0].toUpperCase())

    companion object DiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            Timber.d("itemsTheSame check")
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            Timber.d("contentsTheSame check")
            return oldItem == newItem
        }
    }

}