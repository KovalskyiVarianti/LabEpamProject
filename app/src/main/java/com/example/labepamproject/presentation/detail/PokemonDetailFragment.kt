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
import com.example.labepamproject.domain.Pokemon

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
                    showContent(state.pokemon)
                }
                is PokemonDetailViewState.ErrorState -> {
                    //showErrorMessage(state.errorMessage)
                }
            }
        }
    }

    private fun showContent(pokemon: Pokemon) {
        binding.pokemonDetailImage.loadImage(pokemon.prevImgUrl)
        binding.pokemonDetailName.text = "${pokemon.name}\n" +
                "${pokemon.height}\n" +
                "${pokemon.weight}\n" +
                "${pokemon.experience}"
    }

    private fun ImageView.loadImage(url: String) {
        Glide.with(context)
            .load(url)
            .placeholder(R.drawable.loading_image_placeholder)
            .into(this)
    }
}