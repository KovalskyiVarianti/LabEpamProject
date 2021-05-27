package com.example.labepamproject.presentation.overview.adapter

import com.example.labepamproject.domain.GenerationEntity
import com.example.labepamproject.domain.PokemonEntity

sealed class Item {
    data class PokemonItem(val name: String, val imgSrc: String) : Item()
    data class GenerationItem(val id: Int, val text: String, var isPressed: Boolean = false) :
        Item()
}

fun PokemonEntity.asItem(): Item.PokemonItem = Item.PokemonItem(name, prevImgUrl)
fun GenerationEntity.asItem(): Item.GenerationItem = Item.GenerationItem(id, name)