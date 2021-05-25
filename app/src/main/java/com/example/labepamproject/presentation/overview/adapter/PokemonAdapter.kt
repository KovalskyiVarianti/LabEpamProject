package com.example.labepamproject.presentation.overview.adapter

import android.graphics.Color
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.example.labepamproject.R
import com.example.labepamproject.databinding.ItemPokemonOverviewBinding
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import timber.log.Timber
import kotlin.random.Random

class PokemonAdapter(
    pokemonClickListener: (String, Int) -> Unit,
) :
    AsyncListDifferDelegationAdapter<Item.PokemonItem>(PokemonDiffCallback) {

    init {
        delegatesManager.addDelegate(pokemonAdapterDelegate(pokemonClickListener))
    }

    private fun pokemonAdapterDelegate(pokemonClickListener: (String, Int) -> Unit) =
        adapterDelegateViewBinding<Item.PokemonItem, Item.PokemonItem, ItemPokemonOverviewBinding>(
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
                binding.pokemonOverviewName.text = item.name
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

    companion object PokemonDiffCallback : DiffUtil.ItemCallback<Item.PokemonItem>() {
        override fun areItemsTheSame(
            oldItem: Item.PokemonItem,
            newItem: Item.PokemonItem
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: Item.PokemonItem,
            newItem: Item.PokemonItem
        ): Boolean {
            return oldItem == newItem
        }

    }

}