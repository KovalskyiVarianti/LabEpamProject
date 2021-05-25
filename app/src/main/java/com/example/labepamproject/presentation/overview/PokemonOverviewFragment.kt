package com.example.labepamproject.presentation.overview

import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.bumptech.glide.Glide
import com.example.labepamproject.R
import com.example.labepamproject.databinding.FragmentPokemonOverviewBinding
import com.example.labepamproject.presentation.overview.adapter.GenerationAdapter
import com.example.labepamproject.presentation.overview.adapter.Item
import com.example.labepamproject.presentation.overview.adapter.PokemonAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class PokemonOverviewFragment : Fragment(R.layout.fragment_pokemon_overview) {

    private lateinit var binding: FragmentPokemonOverviewBinding
    private var pokemonAdapter: PokemonAdapter? = null
    private var generationAdapter: GenerationAdapter? = null
    private val viewModel: PokemonOverviewViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        binding = FragmentPokemonOverviewBinding.bind(view)
        provideViewModel()
        providePokemonRecyclerView(
            getSpanCountByOrientation(resources.configuration.orientation),
            providePokemonAdapter()
        )
        provideGenerationRecyclerView(
            provideGenerationAdapter()
        )
        viewModel.fetch()
        setAppName()
    }

    private fun setAppName() {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)
    }

    private fun provideViewModel() {
        viewModel.apply {
            navigateToPokemonDetailFragment().observe(viewLifecycleOwner, ::showPokemonDetails)
            getState().observe(viewLifecycleOwner, ::showState)
            getHeaderText().observe(viewLifecycleOwner, ::showHeader)
        }
    }

    private fun showHeader(headerText: String) {
        binding.headerOverviewText.text = headerText
    }

    private fun showPokemonDetails(pokemonItemParams: Pair<String, Int>?) {
        pokemonItemParams?.let {
            findNavController().navigate(
                PokemonOverviewFragmentDirections
                    .actionPokemonOverviewFragmentToPokemonDetailFragment(
                        pokemonItemParams.first,
                        pokemonItemParams.second
                    )
            )
            viewModel.onPokemonDetailFragmentNavigated()
        }
    }

    private fun showState(state: PokemonOverviewViewState) = when (state) {
        is PokemonOverviewViewState.LoadingState -> {
            showLoadingAnimation()
        }
        is PokemonOverviewViewState.PokemonResultState -> {
            loadPokemons(state.pokemonItems)
        }
        is PokemonOverviewViewState.GenerationResultState -> {
            loadGenerations(state.generationItems)
        }
        is PokemonOverviewViewState.ErrorState -> {
            showErrorMessage(state.errorMessage)
        }
        is PokemonOverviewViewState.LoadingFinishedState -> {
            showContent()
        }
    }

    private fun providePokemonRecyclerView(spanCount: Int, pokemonItemAdapter: PokemonAdapter) {
        pokemonAdapter = pokemonItemAdapter
        binding.pokemonList.apply {
            layoutManager = provideGridLayoutManager(spanCount)
            adapter = pokemonAdapter
            provideScrollListener()
        }
    }

    private fun provideGenerationRecyclerView(generationItemAdapter: GenerationAdapter) {
        generationAdapter = generationItemAdapter
        binding.generationList.adapter = generationAdapter
    }

    private fun RecyclerView.provideScrollListener() {
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_IDLE) {
                    val itemCount = getItemCount(recyclerView)
                    val currentItemNumber = getCurrentItemNumber(recyclerView)
                    Timber.d("$itemCount, $currentItemNumber")
                    if (itemCount == currentItemNumber) {
                        viewModel.loadNextPokemons(ITEMS_PER_PAGE)
                    }
                }
            }

            private fun getItemCount(recyclerView: RecyclerView) =
                recyclerView.adapter?.itemCount

            private fun getCurrentItemNumber(recyclerView: RecyclerView): Int {
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                return layoutManager.findLastCompletelyVisibleItemPosition() + 1
            }
        })
    }

    private fun providePokemonAdapter() = PokemonAdapter(
        viewModel::onPokemonItemClicked,
    )

    private fun provideGenerationAdapter() = GenerationAdapter(
        viewModel::onGenerationItemClicked,
    )

    private fun provideGridLayoutManager(spanCount: Int): GridLayoutManager =
        GridLayoutManager(activity, spanCount)

    private fun getSpanCountByOrientation(orientation: Int) = when (orientation) {
        Configuration.ORIENTATION_PORTRAIT -> SPAN_COUNT_PORTRAIT
        else -> SPAN_COUNT_LANDSCAPE
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

    override fun onDestroyView() {
        super.onDestroyView()
        //binding = null
    }

    private fun loadPokemons(contentList: List<Item.PokemonItem>) {
        pokemonAdapter?.items = contentList
    }

    private fun loadGenerations(contentList: List<Item.GenerationItem>) {
        generationAdapter?.items = contentList
    }

    private fun showErrorMessage(errorMessage: String) {
        binding.loadingStateImage.visibility = View.GONE
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

    private fun showContent() {
        binding.loadingStateImage.visibility = View.GONE
        binding.errorMessage.visibility = View.GONE
    }

}