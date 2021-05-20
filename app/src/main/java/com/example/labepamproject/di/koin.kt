package com.example.labepamproject.di

import com.example.labepamproject.data.NetworkPokemonRepository
import com.example.labepamproject.data.network.POKE_API_URL
import com.example.labepamproject.data.network.PokedexApiService
import com.example.labepamproject.presentation.detail.PokemonDetailViewModel
import com.example.labepamproject.presentation.overview.PokemonOverviewViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val appModule = module {
    single<PokedexApiService> { createPokedexApiService }
    single<NetworkPokemonRepository> { NetworkPokemonRepository(get<PokedexApiService>()) }

    viewModel<PokemonOverviewViewModel> { PokemonOverviewViewModel(get<NetworkPokemonRepository>()) }
    viewModel<PokemonDetailViewModel> { (pokemonName: String) ->
        PokemonDetailViewModel(
            pokemonName,
            get<NetworkPokemonRepository>()
        )
    }
}

private val retrofit = Retrofit.Builder()
    .baseUrl(POKE_API_URL)
    .addConverterFactory(MoshiConverterFactory.create())
    .build()

val createPokedexApiService: PokedexApiService by lazy {
    retrofit.create(PokedexApiService::class.java)
}