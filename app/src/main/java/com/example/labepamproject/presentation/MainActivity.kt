package com.example.labepamproject.presentation

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.labepamproject.R
import com.example.labepamproject.databinding.ActivityMainBinding
import com.example.labepamproject.presentation.adapter.GenerationListAdapter
import com.example.labepamproject.presentation.adapter.Item
import com.example.labepamproject.presentation.adapter.ItemAdapter
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        Timber.i("MainActivity created")
        setContentView(binding.root)

        viewModel = MainViewModel()
        itemAdapter = ItemAdapter(provideGenerationDefaultItem()) {
            Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
        }

        viewModel.getState().observe(this) { state ->
            when (state) {
                is MainViewState.LoadingState -> {
                    showLoadingImage(state.loadingImageId)
                }
                is MainViewState.ResultState -> {
                    showContent(state.items)
                }
                is MainViewState.ErrorState -> {
                    showErrorImage(state.errorImageId)
                }
            }
        }

        binding.pokemonList.layoutManager = provideGridLayoutManager()
        binding.pokemonList.adapter = itemAdapter

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
            Configuration.ORIENTATION_LANDSCAPE -> 6
            else -> 3
        }
        val manager = GridLayoutManager(this, spanCount)
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