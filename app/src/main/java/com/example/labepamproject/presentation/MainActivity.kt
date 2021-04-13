package com.example.labepamproject.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.labepamproject.R
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("MainActivity created")
        setContentView(R.layout.activity_main)
        findViewById<RecyclerView>(R.id.pokemon_list).adapter
    }
}