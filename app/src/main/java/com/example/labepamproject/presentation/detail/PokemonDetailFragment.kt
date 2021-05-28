package com.example.labepamproject.presentation.detail

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.labepamproject.R
import com.example.labepamproject.databinding.FragmentPokemonDetailBinding
import com.example.labepamproject.domain.PokemonEntity
import com.example.labepamproject.presentation.fromCapitalLetter
import com.example.labepamproject.presentation.loadImage
import com.example.labepamproject.presentation.setFragmentTitle
import com.skydoves.progressview.ProgressView
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class PokemonDetailFragment : Fragment(R.layout.fragment_pokemon_detail) {

    private var binding: FragmentPokemonDetailBinding? = null
    private val navArgs by navArgs<PokemonDetailFragmentArgs>()
    private val viewModel: PokemonDetailViewModel by viewModel { parametersOf(navArgs.pokemonName) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        viewModel.fetch()
        setFragmentTitle(
            activity,
            navArgs.pokemonName.fromCapitalLetter()
        ).also { Timber.d("Set name") }
    }

    private fun showContent(pokemonEntity: PokemonEntity) {
        setFragmentTitle(activity, pokemonEntity.name.fromCapitalLetter())
        binding?.let { pokemonDetailBinding ->
            pokemonDetailBinding.apply {
                pokemonDetailImage.setBackgroundColor(navArgs.itemColor)
                pokemonDetailImage.loadImage(pokemonEntity.prevImgUrl)
                experienceBar.labelText = "${pokemonEntity.experience} exp."
                experienceBar.progress = pokemonEntity.experience
                pokemonHeight.text = "Height: ${pokemonEntity.height}"
                pokemonWeight.text = "Weight: ${pokemonEntity.weight}"
                pokemonAbilities.text =
                    "Abilities:\n${pokemonEntity.abilities.joinToString("") { "$it\n" }}"
                pokemonTypes.text =
                    "Types:\n${pokemonEntity.types.joinToString("") { "$it\n" }}"
            }
        }
        pokemonEntity.stats.forEach(::setValues)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setValues(stats: Pair<String, Float>) = binding?.let {
        when (stats.first) {
            "hp" -> {
                it.hpStatBar.setBarStatValue(stats.second)
            }
            "attack" -> {
                it.attackStatBar.setBarStatValue(stats.second)
            }
            "defense" -> {
                it.defenseStatBar.setBarStatValue(stats.second)
            }
            "special-attack" -> {
                it.specialAttackStatBar.setBarStatValue(stats.second)
            }
            "special-defense" -> {
                it.specialDefenseStatBar.setBarStatValue(stats.second)
            }
            "speed" -> {
                it.speedStatBar.setBarStatValue(stats.second)
            }
            else -> throw IllegalArgumentException()
        }
    }

    private fun ProgressView.setBarStatValue(value: Float) {
        labelText = value.toString()
        progress = value
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}