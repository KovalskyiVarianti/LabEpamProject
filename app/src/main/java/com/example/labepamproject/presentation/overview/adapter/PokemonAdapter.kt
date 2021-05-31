package com.example.labepamproject.presentation.overview.adapter

import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import com.example.labepamproject.databinding.ItemPokemonOverviewBinding
import com.example.labepamproject.presentation.fromCapitalLetter
import com.example.labepamproject.presentation.getRandomColor
import com.example.labepamproject.presentation.loadImage
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import timber.log.Timber
import kotlin.random.Random

class PokemonAdapter(
    pokemonClickListener: (imageView: ImageView, String, Int) -> Unit,
) :
    AsyncListDifferDelegationAdapter<Item.PokemonItem>(PokemonDiffCallback) {

    init {
        delegatesManager.addDelegate(pokemonAdapterDelegate(pokemonClickListener))
    }

    private fun pokemonAdapterDelegate(pokemonClickListener: (ImageView, String, Int) -> Unit) =
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
                pokemonClickListener(binding.pokemonOverviewImage, item.name, color)
            }
            bind {
                binding.pokemonOverviewImageCardview.setCardBackgroundColor(color)
                binding.pokemonOverviewName.text = item.name.fromCapitalLetter()
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    binding.pokemonOverviewImage.transitionName = item.name
                }
                binding.pokemonOverviewImage.loadImage(item.imgSrc)
                Timber.d("Pokemon ${item.name} binded")
            }
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