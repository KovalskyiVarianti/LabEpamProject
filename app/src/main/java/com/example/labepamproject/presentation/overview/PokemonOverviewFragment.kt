package com.example.labepamproject.presentation.overview

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.labepamproject.R
import com.example.labepamproject.databinding.FragmentPokemonOverviewBinding
import com.example.labepamproject.presentation.overview.adapter.Item
import com.example.labepamproject.presentation.overview.adapter.ItemAdapter
import timber.log.Timber

class PokemonOverviewFragment : Fragment() {

    private lateinit var binding: FragmentPokemonOverviewBinding
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var viewModel: PokemonOverviewViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPokemonOverviewBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        viewModel = PokemonOverviewViewModel()
        itemAdapter = ItemAdapter(
            provideGenerationDefaultItem(),
            pokemonClickListener = { viewModel.onPokemonItemClicked(it) },
            generationClickListener = {},
        )

        viewModel.navigateToPokemonDetailFragment().observe(viewLifecycleOwner) { pokemonName ->
            pokemonName?.let {
                findNavController().navigate(
                    PokemonOverviewFragmentDirections
                        .actionPokemonOverviewFragmentToPokemonDetailFragment(pokemonName)
                )
                viewModel.onPokemonDetailFragmentNavigated()
            }
        }

        viewModel.getState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PokemonOverviewViewState.LoadingState -> {
                    showLoadingAnimation()
                }
                is PokemonOverviewViewState.ResultState -> {
                    showContent(state.items)
                }
                is PokemonOverviewViewState.ErrorState -> {
                    showErrorMessage(state.errorMessage)
                }
            }
        }

        binding.pokemonList.layoutManager = provideGridLayoutManager(getSpanCount())
        binding.pokemonList.adapter = itemAdapter


    }

    private fun provideGridLayoutManager(spanCount: Int): GridLayoutManager {
        val manager = GridLayoutManager(activity, spanCount)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) = when (position) {
                0 -> spanCount
                1 -> spanCount
                else -> 1
            }
        }
        return manager
    }

    private fun getSpanCount() = when (resources.configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> 3
        else -> 6
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {

            }
        }
        return true
    }

    private fun showContent(contentList: List<Item>) {
        //binding.loadingStateBar.visibility = View.GONE
        binding.loadingStateImage.visibility = View.GONE
        binding.errorMessage.visibility = View.GONE
        itemAdapter.items = contentList.provideHeader(R.string.pokemon_header)
        Timber.d("Data loaded into adapter")
    }

    private fun showErrorMessage(errorMessage: String) {
        binding.loadingStateImage.visibility = View.GONE
        //binding.loadingStateBar.visibility = View.GONE
        binding.errorMessage.text = errorMessage
        binding.errorMessage.visibility = View.VISIBLE
    }

    private fun showLoadingAnimation() {
        Glide.with(binding.loadingStateImage.context)
            .asGif()
            .load(R.drawable.loading_anim)
            .into(binding.loadingStateImage)
        binding.loadingStateImage.visibility = View.VISIBLE
        binding.errorMessage.visibility = View.GONE
    }

    private fun provideGenerationDefaultItem() =
        Item.GenerationItem(getString(R.string.all_generations))

    private fun List<Item>.provideHeader(stringID: Int) =
        listOf(Item.HeaderItem(getString(stringID))) + this
}