package com.example.labepamproject.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.labepamproject.R
import com.example.labepamproject.databinding.ActivityMainBinding
import com.example.labepamproject.databinding.ItemPokemonBinding
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

        viewModel.getPokemonList().observe(this) {
            itemAdapter.items = it
            Timber.i(it.toString())
            Timber.i("Data loaded into adapter")
        }

        viewModel.loadItems()

        binding.pokemonList.layoutManager = provideGridLayoutManager()
        binding.pokemonList.adapter = itemAdapter

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