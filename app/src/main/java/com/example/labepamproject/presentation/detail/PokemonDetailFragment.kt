package com.example.labepamproject.presentation.detail

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.labepamproject.R
import com.example.labepamproject.databinding.FragmentPokemonDetailBinding
import com.example.labepamproject.domain.PokemonEntity
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PokemonDetailFragment : Fragment(R.layout.fragment_pokemon_detail) {

    private lateinit var binding: FragmentPokemonDetailBinding
    private val navArgs by navArgs<PokemonDetailFragmentArgs>()
    private val viewModel: PokemonDetailViewModel by viewModel { parametersOf(navArgs.pokemonName) }

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
        viewModel.fetch()
    }

    private fun showContent(pokemonEntity: PokemonEntity) {
        binding.pokemonDetailImage.loadImage(pokemonEntity.prevImgUrl)
        binding.pokemonDetailName.text = "name: ${pokemonEntity.name}\n" +
                "height: ${pokemonEntity.height}\n" +
                "weight: ${pokemonEntity.weight}\n" +
                "experience: ${pokemonEntity.experience}\n" +
                "abilities: ${pokemonEntity.abilities.joinToString { it }}\n" +
                "types: ${pokemonEntity.types.joinToString { it }}\n" +
                "stats: ${pokemonEntity.stats.joinToString { it.first }}\n"
    }

    private fun ImageView.loadImage(url: String) =
        Glide.with(context)
            .load(url)
            .placeholder(R.drawable.loading_image_placeholder)
            .into(this)

}