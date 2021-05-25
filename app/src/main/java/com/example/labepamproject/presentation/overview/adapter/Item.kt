package com.example.labepamproject.presentation.overview.adapter

import com.example.labepamproject.domain.GenerationEntity
import com.example.labepamproject.domain.PokemonEntity

sealed class Item {
    data class PokemonItem(val name: String, val imgSrc: String) : Item() {
        companion object {
            fun PokemonEntity.asItem(): PokemonItem = PokemonItem(name, prevImgUrl)
        }
    }

    data class GenerationItem(val id: Int, val text: String, var isPressed: Boolean = false) :
        Item() {
        companion object {
            fun GenerationEntity.asItem(): GenerationItem = GenerationItem(id, name)
        }
    }
}