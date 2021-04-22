package com.example.labepamproject.presentation.overview.adapter

import com.example.labepamproject.databinding.ItemGenerationBinding
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import timber.log.Timber
import java.util.*

class GenerationListAdapter(generationClickListener: (Int) -> Unit) :
    ListDelegationAdapter<List<Item>>() {
    init {
        delegatesManager.addDelegate(generationAdapterDelegate(generationClickListener))
    }

    private fun generationAdapterDelegate(generationClickListener: (Int) -> Unit) =
        adapterDelegateViewBinding<Item.GenerationItem, Item, ItemGenerationBinding>(
            { layoutInflater, parent ->
                ItemGenerationBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            }
        ) {
            binding.root.setOnClickListener {
                generationClickListener(getGenerationId(item.text))
            }
            bind {
                binding.generationName.text = adaptText(item.text)
                Timber.d("Generation $item binded")
            }
        }

    private fun getGenerationId(text: String) = when (text) {
        "generation-i" -> 1
        "generation-ii" -> 2
        "generation-iii" -> 3
        "generation-iv" -> 4
        "generation-v" -> 5
        "generation-vi" -> 6
        "generation-vii" -> 7
        "generation-viii" -> 8
        else -> 0
    }

    fun adaptText(text: String): String = text.toUpperCase(Locale.ROOT)
}