package com.example.labepamproject.presentation.adapter

import com.example.labepamproject.databinding.ItemGenerationBinding
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import timber.log.Timber

class GenerationListAdapter : ListDelegationAdapter<List<Item>>() {
    init {
        delegatesManager.addDelegate(generationAdapterDelegate())
    }

    private fun generationAdapterDelegate() = adapterDelegateViewBinding<Item.GenerationItem, Item, ItemGenerationBinding>(
        { layoutInflater, parent -> ItemGenerationBinding.inflate(layoutInflater, parent,false) }
    ) {
        bind {
            binding.generationName.text = item.text
            Timber.i("Generation binded")
        }
    }
}