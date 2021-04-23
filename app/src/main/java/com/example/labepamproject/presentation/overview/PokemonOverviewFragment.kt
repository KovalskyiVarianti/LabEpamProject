package com.example.labepamproject.presentation.overview

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
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
                    showLoadingBar()
                }
                is PokemonOverviewViewState.ResultState -> {
                    showContent(state.items)
                }
                is PokemonOverviewViewState.ErrorState -> {
                    showErrorImage(state.errorMessage)
                }
            }
        }

        binding.pokemonList.layoutManager = provideGridLayoutManager(getSpanCount())
        binding.pokemonList.adapter = itemAdapter

        return binding.root
    }

    private fun provideGridLayoutManager(spanCount : Int): GridLayoutManager {
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
        binding.loadingStateBar.visibility = View.GONE
        binding.errorStateImage.visibility = View.GONE
        itemAdapter.items = contentList.provideHeader(R.string.pokemon_header)
        Timber.d(contentList.joinToString { item -> "$item\n" })
        Timber.d("Data loaded into adapter")
    }

    private fun showErrorImage(errorMessage: String) {
        binding.loadingStateBar.visibility = View.GONE
        binding.errorStateImage.text = errorMessage
        binding.errorStateImage.visibility = View.VISIBLE
    }

    private fun showLoadingBar() {
        binding.loadingStateBar.visibility = View.VISIBLE
        binding.errorStateImage.visibility = View.GONE
    }

    private fun provideGenerationDefaultItem() =
        Item.GenerationItem(getString(R.string.all_generations))

    private fun List<Item>.provideHeader(stringID: Int) =
        listOf(Item.HeaderItem(getString(stringID))) + this
}