package com.example.labepamproject.presentation

import android.graphics.Color
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.example.labepamproject.R
import kotlin.random.Random

val backgroundColorRGB = Color.rgb(135, 220, 246)
val clickableItemColorRGB = Color.rgb(200, 253, 223)
fun ImageView.loadImage(url: String) =
    Glide.with(context)
        .load(url)
        .placeholder(R.drawable.loading_image_placeholder)
        .into(this)

fun generateUrlFromId(id: Int): String =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"

fun setFragmentTitle(activity: FragmentActivity?, title: String) {
    (activity as AppCompatActivity).supportActionBar?.title = title
}

fun Random.getRandomColor() = Color.rgb(
    nextInt(256),
    nextInt(256),
    nextInt(256)
)