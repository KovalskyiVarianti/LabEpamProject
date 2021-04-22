package com.example.labepamproject.presentation.overview

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.labepamproject.R
import com.example.labepamproject.databinding.FragmentPokemonOverviewBinding
import com.example.labepamproject.presentation.overview.adapter.Item
import com.example.labepamproject.presentation.overview.adapter.ItemAdapter
import timber.log.Timber
import kotlin.properties.Delegates

//REFACTORING REQUIRED
private const val SPAN_COUNT_PORTRAIT = "SPAN_COUNT_PORTRAIT"
private const val SPAN_COUNT_LANDSCAPE = "SPAN_COUNT_LANDSCAPE"

//REFACTORING REQUIRED
private const val SPAN_COUNT_PORTRAIT_DEFAULT = 3
private const val SPAN_COUNT_LANDSCAPE_DEFAULT = 6

class PokemonOverviewFragment : Fragment() {

    private lateinit var binding: FragmentPokemonOverviewBinding
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var viewModel: PokemonOverviewViewModel

    //REFACTORING REQUIRED
    private var spanCountLandscape by Delegates.notNull<Int>()
    private var spanCountPortrait by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPokemonOverviewBinding.inflate(layoutInflater, container, false)
        setHasOptionsMenu(true)
        readGridLayoutManagerSettings()
        Timber.d("(Load) SpanCountPortrait: $spanCountPortrait, SpanCountLandscape: $spanCountLandscape")
        viewModel = PokemonOverviewViewModel()
        itemAdapter = ItemAdapter(
            provideGenerationDefaultItem(),
            pokemonClickListener = {},
            generationClickListener = {},
        )

        viewModel.getState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PokemonOverviewViewState.LoadingState -> {
                    showLoadingImage(state.loadingImageId)
                }
                is PokemonOverviewViewState.ResultState -> {
                    showContent(state.items)
                }
                is PokemonOverviewViewState.ErrorState -> {
                    showErrorImage(state.errorImageId)
                }
            }
        }

        binding.pokemonList.layoutManager = provideGridLayoutManager()
        binding.pokemonList.adapter = itemAdapter

        return binding.root
    }

    private fun readGridLayoutManagerSettings() {
        val preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        spanCountPortrait = preferences.getInt(SPAN_COUNT_PORTRAIT, SPAN_COUNT_PORTRAIT_DEFAULT)
        spanCountLandscape = preferences.getInt(SPAN_COUNT_LANDSCAPE, SPAN_COUNT_LANDSCAPE_DEFAULT)
    }

    override fun onDestroy() {
        //REFACTORING REQUIRED
        requireActivity().getPreferences(Context.MODE_PRIVATE).edit {
            putInt(SPAN_COUNT_PORTRAIT, spanCountPortrait)
            putInt(SPAN_COUNT_LANDSCAPE, spanCountLandscape)
            Timber.d("(Save) SpanCountPortrait: $spanCountPortrait, SpanCountLandscape: $spanCountLandscape")
            apply()
        }
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.Big_items -> {
                spanCountPortrait = 2
                spanCountLandscape = 4
            }
            R.id.small_items -> {
                spanCountPortrait = 3
                spanCountLandscape = 6
            }
        }
        binding.pokemonList.layoutManager = provideGridLayoutManager()
        return true
    }

    private fun showContent(contentList: List<Item>) {
        binding.stateImage.visibility = View.GONE
        itemAdapter.items = contentList.provideHeader(R.string.pokemon_header)
        Timber.d(contentList.joinToString { item -> "$item\n" })
        Timber.d("Data loaded into adapter")
    }

    private fun showErrorImage(errorImageId: Int) {
        binding.stateImage.visibility = View.VISIBLE
        binding.stateImage.setImageResource(errorImageId)
    }

    private fun showLoadingImage(loadingImageId: Int) {
        binding.stateImage.visibility = View.VISIBLE
        binding.stateImage.setImageResource(loadingImageId)
    }

    private fun provideGridLayoutManager(): GridLayoutManager {
        val spanCount = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> spanCountLandscape
            else -> spanCountPortrait
        }
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

    private fun provideGenerationDefaultItem() =
        Item.GenerationItem(getString(R.string.all_generations))

    private fun List<Item>.provideHeader(stringID: Int) =
        listOf(Item.HeaderItem(getString(stringID))) + this
}