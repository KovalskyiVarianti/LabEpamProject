package com.example.labepamproject.presentation.adapter

import com.example.labepamproject.domain.Pokemon

sealed class Item {
    data class PokemonItem(val name: String, val imgSrc: String) : Item() {
        companion object {
            fun Pokemon.asItem(): PokemonItem = PokemonItem(name, prevImgUrl)
        }
    }

    data class HeaderItem(val text: String) : Item()
}