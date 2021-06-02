package com.example.labepamproject.presentation.wiki

import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.labepamproject.R
import com.example.labepamproject.databinding.FragmentPokemonWikiBinding
import com.example.labepamproject.presentation.fromCapitalLetter
import com.example.labepamproject.presentation.setFragmentTitle

class PokemonWikiFragment : Fragment(R.layout.fragment_pokemon_wiki) {

    private var binding: FragmentPokemonWikiBinding? = null
    private val navArgs by navArgs<PokemonWikiFragmentArgs>()
    private val wikiUrl: String = "https://pokemon.fandom.com/wiki/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentTitle(
            activity,
            navArgs.pokemonName.fromCapitalLetter()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPokemonWikiBinding.bind(view)
        provideWebView()
    }

    private fun provideWebView() {
        binding?.let {
            it.webview.settings.javaScriptEnabled = true
            it.webview.settings.loadsImagesAutomatically = true
            it.webview.webViewClient = object : WebViewClient() {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    return false
                }
            }
            it.webview.loadUrl(wikiUrl + navArgs.pokemonName)
        }
    }
}