package com.example.labepamproject.presentation.adapter

import com.example.labepamproject.databinding.ItemGenerationBinding
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import timber.log.Timber
import java.util.*
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

    private fun adaptText(text: String): String = text.toUpperCase(Locale.ROOT)
}