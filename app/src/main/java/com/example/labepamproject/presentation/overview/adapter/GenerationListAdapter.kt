package com.example.labepamproject.presentation.overview.adapter

import com.example.labepamproject.databinding.ItemGenerationOverviewBinding
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import timber.log.Timber

class GenerationListAdapter(generationClickListener: (Int, String) -> Unit) :
    AsyncListDifferDelegationAdapter<Item>(ItemAdapter.DiffCallback) {
    init {
        delegatesManager.addDelegate(generationAdapterDelegate(generationClickListener))
    }

    private fun generationAdapterDelegate(generationClickListener: (Int, String) -> Unit) =
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
                generationClickListener(item.id, item.text)
                Timber.d("$")
            }
            bind {
                binding.generationOverviewName.text = item.text
                Timber.d("Generation $item binded")
            }
        }
}