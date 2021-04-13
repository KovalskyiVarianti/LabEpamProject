package com.example.labepamproject.presentation

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.labepamproject.domain.Pokemon
import com.example.labepamproject.R
import timber.log.Timber
import kotlin.random.Random


private const val ITEM_VIEW_TYPE_POKEMON = 0

class PokemonAdapter(data: List<Pokemon>) :
    ListAdapter<Item, RecyclerView.ViewHolder>(PokemonDiffCallback()) {

    init {
        submitList(data.map { Item.PokemonItem(it) })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_POKEMON ->
                PokemonViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.pokemon_layout, parent, false)
                ).also { Timber.i("PokemonViewHolder created") }

            else -> throw Exception()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PokemonViewHolder -> {
                val pokemonItem = getItem(position) as Item.PokemonItem
                holder.bind(pokemonItem.pokemon).also { Timber.i("PokemonViewHolder bind") }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Item.PokemonItem -> ITEM_VIEW_TYPE_POKEMON
        }
    }

    class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val name: TextView = itemView.findViewById(R.id.pokemon_item)

        fun bind(item: Pokemon) {
            name.text = item.name
            val rnd = Random
            name.setTextColor(
                Color.argb(
                    255,
                    rnd.nextInt(256),
                    rnd.nextInt(256),
                    rnd.nextInt(256)
                )
            )
        }

    }
}

class PokemonDiffCallback : DiffUtil.ItemCallback<Item>() {

    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }

}

sealed class Item {
    data class PokemonItem(val pokemon: Pokemon) : Item()
}