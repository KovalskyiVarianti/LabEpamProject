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
import com.example.labepamproject.presentation.overview.adapter.Item
import com.example.labepamproject.presentation.overview.adapter.ItemAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

const val ITEMS_PER_PAGE: Int = 24
const val SPAN_COUNT_PORTRAIT: Int = 3
const val SPAN_COUNT_LANDSCAPE: Int = 6

class PokemonOverviewFragment : Fragment(R.layout.fragment_pokemon_overview) {

    private lateinit var binding: FragmentPokemonOverviewBinding
    private lateinit var itemAdapter: ItemAdapter
    private val viewModel: PokemonOverviewViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        binding = FragmentPokemonOverviewBinding.bind(view)
        provideViewModel()
        provideRecyclerView(getSpanCountByOrientation(resources.configuration.orientation))
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
        }
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
        is PokemonOverviewViewState.ResultState -> {
            loadContent(state.items)
        }
        is PokemonOverviewViewState.ErrorState -> {
            showErrorMessage(state.errorMessage)
        }
        is PokemonOverviewViewState.LoadingFinishedState -> {
            showContent()
        }
    }

    private fun provideRecyclerView(spanCount: Int) {
        itemAdapter = provideItemAdapter()
        binding.pokemonList.apply {
            layoutManager = provideGridLayoutManager(spanCount)
            adapter = itemAdapter
            provideScrollListener()
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

    private fun provideItemAdapter() = ItemAdapter(
        viewModel::onPokemonItemClicked,
        generationClickListener = {},
    )

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

    private fun loadContent(contentList: List<Item>) {
        itemAdapter.items = viewModel.loadData(contentList)
        Timber.d("Data loaded into adapter")
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