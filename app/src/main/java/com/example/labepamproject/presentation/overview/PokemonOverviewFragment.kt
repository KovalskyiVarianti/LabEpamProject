package com.example.labepamproject.presentation.overview

import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.bumptech.glide.Glide
import com.example.labepamproject.R
import com.example.labepamproject.databinding.FragmentPokemonOverviewBinding
import com.example.labepamproject.presentation.overview.adapter.GenerationAdapter
import com.example.labepamproject.presentation.overview.adapter.Item
import com.example.labepamproject.presentation.overview.adapter.PokemonAdapter
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

const val DEFAULT_HEADER_TEXT = "ALL"
const val ITEMS_PER_PAGE: Int = 24
const val SPAN_COUNT_DEFAULT_VALUE = 3

class PokemonOverviewFragment : Fragment(R.layout.fragment_pokemon_overview) {

    private var binding: FragmentPokemonOverviewBinding? = null
    private var pokemonAdapter: PokemonAdapter? = null
    private var generationAdapter: GenerationAdapter? = null
    private val viewModel: PokemonOverviewViewModel by viewModel()
    private var sharedPreferences: SharedPreferences? = null
    private var spanCount: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding = FragmentPokemonOverviewBinding.bind(view)
        provideViewModel()
        sharedPreferences = provideSharedPreferences()
        provideSpanCount()
        providePokemonRecyclerView(
            getSpanCountByOrientation(resources.configuration.orientation),
            providePokemonAdapter()
        )
        provideGenerationRecyclerView(
            provideGenerationAdapter()
        )
        viewModel.fetch()
        waitForTransition(binding?.generationList)
    }

    private fun Fragment.waitForTransition(targetView: View?) {
        postponeEnterTransition()
        targetView?.doOnPreDraw { startPostponedEnterTransition() }
    }

    private fun provideSharedPreferences() = PreferenceManager.getDefaultSharedPreferences(activity)

    private fun provideSpanCount() {
        spanCount = sharedPreferences?.getString(getString(R.string.sp_key_item_size), "3")?.toInt()
    }

    private fun provideViewModel() {
        viewModel.apply {
            navigateToPokemonDetailFragment().observe(viewLifecycleOwner, ::showPokemonDetails)
            getState().observe(viewLifecycleOwner, ::showState)
            getHeaderText().observe(viewLifecycleOwner, ::showHeader)
        }
    }

    private fun showHeader(headerText: String) {
        binding?.let { it.headerOverviewText.text = headerText }
    }

    private fun showPokemonDetails(pokemonItemParams: Triple<ImageView, String, Int>?) {
        pokemonItemParams?.let {
            findNavController().navigate(
                PokemonOverviewFragmentDirections
                    .actionPokemonOverviewFragmentToPokemonDetailFragment(
                        pokemonItemParams.second,
                        pokemonItemParams.third
                    ),
                FragmentNavigatorExtras(
                    pokemonItemParams.first to pokemonItemParams.second
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
            stopLoading()
        }
    }

    private fun providePokemonRecyclerView(spanCount: Int, pokemonItemAdapter: PokemonAdapter) {
        pokemonAdapter = pokemonItemAdapter
        binding?.let {
            it.pokemonList.apply {
                layoutManager = provideGridLayoutManager(spanCount)
                adapter = pokemonAdapter
                provideScrollListener()
            }
        }
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
                        viewModel.loadNextPokemons(itemCount)
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

    private fun provideGenerationRecyclerView(generationItemAdapter: GenerationAdapter) {
        generationAdapter = generationItemAdapter
        binding?.let { it.generationList.adapter = generationAdapter }
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
        Configuration.ORIENTATION_PORTRAIT -> spanCount ?: SPAN_COUNT_DEFAULT_VALUE
        else -> spanCount?.times(2) ?: SPAN_COUNT_DEFAULT_VALUE * 2
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                this?.let { findNavController().navigate(PokemonOverviewFragmentDirections.actionPokemonOverviewFragmentToSettingsFragment()) }
            }
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun loadPokemons(contentList: List<Item.PokemonItem>) {
        pokemonAdapter?.items = contentList
    }

    private fun loadGenerations(contentList: List<Item.GenerationItem>) {
        generationAdapter?.items = contentList
    }

    private fun showErrorMessage(errorMessage: String) {
        binding?.let { binding ->
            binding.loadingStateImage.visibility = View.GONE
            Snackbar.make(
                binding.root.rootView, errorMessage, Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun showLoadingAnimation() {
        binding?.let {
            Glide.with(it.loadingStateImage.context)
                .asGif()
                .load(R.drawable.loading_anim)
                .into(it.loadingStateImage)
            it.loadingStateImage.visibility = View.VISIBLE
            it.generationList.isFocusable = false
        }
    }

    private fun stopLoading() {
        binding?.let {
            it.loadingStateImage.visibility = View.GONE
        }
    }

}