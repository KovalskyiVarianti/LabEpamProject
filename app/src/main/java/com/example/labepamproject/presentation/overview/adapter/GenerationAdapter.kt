package com.example.labepamproject.presentation.overview.adapter

import android.graphics.Color
import androidx.recyclerview.widget.DiffUtil
import com.example.labepamproject.databinding.ItemGenerationOverviewBinding
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
                item.isPressed = true
                notifyItemChanged(adapterPosition)
                items.forEach {
                    if (it != item && it.isPressed) {
                        it.isPressed = false
                    }
                    notifyDataSetChanged()
                }
            }
            bind {
                if (!item.isPressed) {
                    binding.root.setCardBackgroundColor(Color.rgb(200, 253, 223))
                } else {
                    binding.root.setCardBackgroundColor(Color.rgb(135, 220, 246))
                }
                binding.generationOverviewName.text = item.text
                Timber.d("Generation $item binded")
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