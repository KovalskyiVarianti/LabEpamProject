package com.example.labepamproject.presentation.detail

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.labepamproject.R
import com.example.labepamproject.databinding.FragmentPokemonDetailBinding
import com.example.labepamproject.domain.PokemonEntity
import com.skydoves.progressview.ProgressView
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

class PokemonDetailFragment : Fragment(R.layout.fragment_pokemon_detail) {

    private lateinit var binding: FragmentPokemonDetailBinding
    private val navArgs by navArgs<PokemonDetailFragmentArgs>()
    private val viewModel: PokemonDetailViewModel by viewModel { parametersOf(navArgs.pokemonName) }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentPokemonDetailBinding.bind(view)
        viewModel.getState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PokemonDetailViewState.LoadingState -> {
                    //showLoadingAnimation()
                }
                is PokemonDetailViewState.ResultState -> {
                    showContent(state.pokemonEntity)
                }
                is PokemonDetailViewState.ErrorState -> {
                    //showErrorMessage(state.errorMessage)
                }
            }
        }
        (activity as AppCompatActivity).supportActionBar?.title = navArgs.pokemonName
        viewModel.fetch()
        setPokemonName()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setPokemonName() {
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.title = navArgs.pokemonName.toUpperCase(Locale.ROOT)
    }

    private fun showContent(pokemonEntity: PokemonEntity) {
        binding.pokemonDetailImage.setBackgroundColor(navArgs.itemColor)
        binding.pokemonDetailImage.loadImage(pokemonEntity.prevImgUrl)
        binding.pokemonDetailName.text = "name: ${pokemonEntity.name}\n" +
                "height: ${pokemonEntity.height}\n" +
                "weight: ${pokemonEntity.weight}\n" +
                "experience: ${pokemonEntity.experience}\n" +
                "abilities: ${pokemonEntity.abilities.joinToString { it }}\n" +
                "types: ${pokemonEntity.types.joinToString { it }}\n"
        pokemonEntity.stats.forEach(::setValues)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setValues(stats: Pair<String, Float>) = when (stats.first) {
        "hp" -> {
            //binding.hpStatText.setTextStatName(stats.first)
            binding.hpStatBar.setBarStatValue(stats.second)
        }
        "attack" -> {
            //binding.attackStatText.setTextStatName(stats.first)
            binding.attackStatBar.setBarStatValue(stats.second)
        }
        "defense" -> {
            //binding.defenseStatText.setTextStatName(stats.first)
            binding.defenseStatBar.setBarStatValue(stats.second)
        }
        "special-attack" -> {
            //binding.specialAttackStatText.setTextStatName(stats.first)
            binding.specialAttackStatBar.setBarStatValue(stats.second)
        }
        "special-defense" -> {
            binding.specialDefenseStatBar.setBarStatValue(stats.second)
        }
        "speed" -> {
            binding.speedStatBar.setBarStatValue(stats.second)
        }
        else -> throw IllegalArgumentException()
    }

    private fun TextView.setTextStatName(name: String) {
        text = name
    }

    private fun ProgressView.setBarStatValue(value: Float) {
        labelText = value.toString()
        progress = value
    }

    private fun ImageView.loadImage(url: String) =
        Glide.with(context)
            .load(url)
            .placeholder(R.drawable.loading_image_placeholder)
            .into(this)

}