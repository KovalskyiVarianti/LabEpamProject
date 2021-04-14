package com.example.labepamproject.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.labepamproject.R
import com.example.labepamproject.databinding.ActivityMainBinding
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
        }
        viewModel.loadItems()
        findViewById<RecyclerView>(R.id.pokemon_list).adapter = itemAdapter
    }
}