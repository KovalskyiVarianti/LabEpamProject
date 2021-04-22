package com.example.labepamproject.presentation.overview.adapter

import com.example.labepamproject.databinding.ItemGenerationOverviewBinding
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import timber.log.Timber
import java.util.*

class GenerationListAdapter(generationClickListener: (Int) -> Unit) :
    AsyncListDifferDelegationAdapter<Item>(ItemAdapter.DiffCallback) {
    init {
        delegatesManager.addDelegate(generationAdapterDelegate(generationClickListener))
    }

    private fun generationAdapterDelegate(generationClickListener: (Int) -> Unit) =
        adapterDelegateViewBinding<Item.GenerationItem, Item, ItemGenerationOverviewBinding>(
            { layoutInflater, parent ->
                ItemGenerationOverviewBinding.inflate(
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
                binding.generationOverviewName.text = adaptText(item.text)
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

    private fun adaptText(text: String): String = text.toUpperCase(Locale.ROOT)
}