package com.example.labepamproject.presentation.adapter

import com.example.labepamproject.databinding.ItemGenerationBinding
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import timber.log.Timber
import kotlin.IllegalArgumentException

class GenerationListAdapter : ListDelegationAdapter<List<Item>>() {
    init {
        delegatesManager.addDelegate(generationAdapterDelegate())
    }

    private fun generationAdapterDelegate() =
        adapterDelegateViewBinding<Item.GenerationItem, Item, ItemGenerationBinding>(
            { layoutInflater, parent ->
                ItemGenerationBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            }
        ) {
            bind {
                binding.generationName.text = adaptText(item.text)
                Timber.i("Generation binded")
            }
        }

    private fun adaptText(text: String): String = when (text) {
        "generation-i" -> "Generation 1"
        "generation-ii" -> "Generation 2"
        "generation-iii" -> "Generation 3"
        "generation-iv" -> "Generation 4"
        "generation-v" -> "Generation 5"
        "generation-vi" -> "Generation 6"
        "generation-vii" -> "Generation 7"
        "generation-viii" -> "Generation 8"
        else -> "All generations"
    }
}