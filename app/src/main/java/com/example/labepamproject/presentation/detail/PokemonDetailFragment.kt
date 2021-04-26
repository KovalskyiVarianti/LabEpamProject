package com.example.labepamproject.presentation.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.labepamproject.R
import com.example.labepamproject.databinding.FragmentPokemonDetailBinding
import com.example.labepamproject.domain.PokemonEntity

class PokemonDetailFragment : Fragment() {

    private lateinit var binding: FragmentPokemonDetailBinding
    private lateinit var viewModel: PokemonDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPokemonDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val pokemonName = PokemonDetailFragmentArgs.fromBundle(requireArguments()).pokemonName
        viewModel = PokemonDetailViewModel(pokemonName)
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
    }

    private fun showContent(pokemonEntity: PokemonEntity) {
        binding.pokemonDetailImage.loadImage(pokemonEntity.prevImgUrl)
        binding.pokemonDetailName.text = "name: ${pokemonEntity.name}\n" +
                "height: ${pokemonEntity.height}\n" +
                "weight: ${pokemonEntity.weight}\n" +
                "experience: ${pokemonEntity.experience}\n" +
                "abilities: ${pokemonEntity.abilities.joinToString { it }}\n" +
                "stats: ${pokemonEntity.stats.joinToString { it.toString() }}\n" +
                "types: ${pokemonEntity.types.joinToString { it }}\n"
    }

    private fun ImageView.loadImage(url: String) =
        Glide.with(context)
            .load(url)
            .placeholder(R.drawable.loading_image_placeholder)
            .into(this)

}