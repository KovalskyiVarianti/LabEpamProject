package com.example.labepamproject.presentation.detail

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.example.labepamproject.R
import com.example.labepamproject.databinding.FragmentPokemonDetailBinding
import com.example.labepamproject.domain.PokemonEntity
import com.example.labepamproject.presentation.fromCapitalLetter
import com.example.labepamproject.presentation.loadImage
import com.example.labepamproject.presentation.setFragmentTitle
import com.google.android.material.snackbar.Snackbar
import com.skydoves.progressview.ProgressView
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class PokemonDetailFragment : Fragment(R.layout.fragment_pokemon_detail) {

    private var binding: FragmentPokemonDetailBinding? = null
    private val navArgs by navArgs<PokemonDetailFragmentArgs>()
    private val viewModel: PokemonDetailViewModel by viewModel { parametersOf(navArgs.pokemonName) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentTitle(
            activity,
            navArgs.pokemonName.fromCapitalLetter()
        )
        provideSharedElementEnterTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPokemonDetailBinding.bind(view)
        setTransitionNameForImage()
        provideViewModel()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.pokemon_detail_menu, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.pokemon_share) {
            startActivity(
                viewModel.buildShareIntent(
                    ShareCompat.IntentBuilder(requireContext()),
                    getString(R.string.pokemon_share_message, navArgs.pokemonName)
                )
            )
        }
        return super.onOptionsItemSelected(item)
    }

    private fun provideSharedElementEnterTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Timber.d("Transition")
            sharedElementEnterTransition =
                TransitionInflater.from(context)
                    .inflateTransition(R.transition.item_scope_transition)
        }
    }

    private fun setTransitionNameForImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding?.pokemonDetailImage?.transitionName = navArgs.pokemonName
        }
    }

    private fun provideViewModel() {
        viewModel.apply {
            navigateToPokemonWikiFragment().observe(viewLifecycleOwner, ::showPokemonWiki)
            getState().observe(viewLifecycleOwner, ::showState)
            fetch()
        }
    }

    private fun showState(state: PokemonDetailViewState) = when (state) {
        is PokemonDetailViewState.LoadingState -> {
            showLoadingAnimation()
        }
        is PokemonDetailViewState.ResultState -> {
            showContent(state.pokemonEntity)
        }
        is PokemonDetailViewState.ErrorState -> {
            showErrorMessage(state.errorMessage)
        }
    }


    private fun showPokemonWiki(pokemonName: String?) {
        pokemonName?.let {
            findNavController().navigate(
                PokemonDetailFragmentDirections.actionPokemonDetailFragmentToPokemonWikiFragment(it)
            )
            viewModel.onPokemonWikiFragmentNavigated()
        }
    }

    private fun showErrorMessage(errorMessage: String) {
        binding?.let { binding ->
            Snackbar.make(
                binding.root.rootView, errorMessage, Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun showLoadingAnimation() {

    }

    private fun showContent(pokemonEntity: PokemonEntity) {
        setFragmentTitle(activity, pokemonEntity.name.fromCapitalLetter())
        binding?.let { pokemonDetailBinding ->
            pokemonDetailBinding.apply {
                pokemonCardview.setCardBackgroundColor(navArgs.itemColor)
                pokemonDetailImage.loadImage(pokemonEntity.prevImgUrl)
                experienceBar.labelText = "${pokemonEntity.experience} exp."
                experienceBar.progress = pokemonEntity.experience
                pokemonHeight.text = getString(R.string.pokemon_height_text, pokemonEntity.height)
                pokemonWeight.text = getString(R.string.pokemon_weight_text, pokemonEntity.weight)
                pokemonAbilities.text = getString(
                    R.string.pokemon_abilities_text,
                    pokemonEntity.abilities.joinToString("") { "$it\n" })
                pokemonTypes.text = getString(
                    R.string.pokemon_types_text,
                    pokemonEntity.types.joinToString("") { "$it\n" })
                moreButton.setOnClickListener {
                    viewModel.onMoreButtonClicked(pokemonEntity.name)
                }
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