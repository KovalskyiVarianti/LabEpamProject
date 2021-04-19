package com.example.labepamproject.presentation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.labepamproject.databinding.ActivityMainBinding
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
        itemAdapter = ItemAdapter()

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

    private fun showContent(contentList: List<Item>){
        binding.stateImage.visibility = View.GONE
        itemAdapter.items = contentList
        Timber.d(contentList.joinToString { item -> "$item\n" })
        Timber.i("Data loaded into adapter")
    }

    private fun showErrorImage(errorImageId: Int) {
        binding.stateImage.visibility = View.VISIBLE
        binding.stateImage.setImageResource(errorImageId)
    }

    private fun showLoadingImage(loadingImageId: Int) {
        binding.stateImage.visibility = View.VISIBLE
        binding.stateImage.setImageResource(loadingImageId) //image
    }

    private fun provideGridLayoutManager(): GridLayoutManager {
        val manager = GridLayoutManager(this, 2)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) = when (position) {
                0 -> 2
                else -> 1
            }
        }
        return manager
    }
}