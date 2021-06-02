package com.example.labepamproject.presentation.overview.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.labepamproject.databinding.ItemGenerationOverviewBinding
import com.example.labepamproject.presentation.backgroundColorRGB
import com.example.labepamproject.presentation.clickableItemColorRGB
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import timber.log.Timber

class GenerationAdapter(generationClickListener: (Int, String) -> Unit) :
    AsyncListDifferDelegationAdapter<Item.GenerationItem>(GenerationDiffCallback) {
    init {
        delegatesManager.addDelegate(generationAdapterDelegate(generationClickListener))
    }

    private fun generationAdapterDelegate(generationClickListener: (Int, String) -> Unit) =
        adapterDelegateViewBinding<Item.GenerationItem, Item.GenerationItem, ItemGenerationOverviewBinding>(
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
                Timber.d("$item is clicked")
                setFlags(item)
                notifyDataSetChanged()
            }
            bind {
                checkFlags(item, binding)
                binding.generationOverviewName.text = item.text
                Timber.d("Generation $item binded")
            }
        }

    private fun setFlags(item: Item.GenerationItem) {
        item.isPressed = true
        items.forEach {
            if (it != item) {
                it.isPressed = false
            }
        }
    }

    private fun checkFlags(item: Item.GenerationItem, binding: ItemGenerationOverviewBinding) {
        if (item.isPressed) {
            binding.root.setCardBackgroundColor(backgroundColorRGB)
            binding.root.isClickable = false
        } else {
            binding.root.isClickable = true
            binding.root.setCardBackgroundColor(clickableItemColorRGB)
        }
    }

    companion object GenerationDiffCallback : DiffUtil.ItemCallback<Item.GenerationItem>() {
        override fun areItemsTheSame(
            oldItem: Item.GenerationItem,
            newItem: Item.GenerationItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Item.GenerationItem,
            newItem: Item.GenerationItem
        ): Boolean {
            return oldItem == newItem
        }


    }
}