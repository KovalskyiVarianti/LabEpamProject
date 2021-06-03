package com.example.labepamproject.di

import android.content.Context
import androidx.room.Room
import com.example.labepamproject.data.NetworkRoomPokemonRepository
import com.example.labepamproject.data.database.PokemonDatabase
import com.example.labepamproject.data.network.POKE_API_URL
import com.example.labepamproject.data.network.PokedexApiService
import com.example.labepamproject.presentation.detail.PokemonDetailViewModel
import com.example.labepamproject.presentation.overview.PokemonOverviewViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val appModule = module {
    single<PokedexApiService> { createPokedexApiService }
    single<PokemonDatabase> { getDatabase(androidApplication()) }
   // single<NetworkPokemonRepository> { NetworkPokemonRepository(get<PokedexApiService>()) }
    single<NetworkRoomPokemonRepository> {
        NetworkRoomPokemonRepository(
            get<PokedexApiService>(),
            get<PokemonDatabase>()
        )
    }

    // viewModel<PokemonOverviewViewModel> { PokemonOverviewViewModel(get<NetworkPokemonRepository>()) }
    viewModel<PokemonOverviewViewModel> { PokemonOverviewViewModel(get<NetworkRoomPokemonRepository>()) }
    viewModel<PokemonDetailViewModel> { (pokemonName: String) ->
        PokemonDetailViewModel(
            pokemonName,
            get<NetworkRoomPokemonRepository>()
        )
    }
}

//Retrofit
private val retrofit = Retrofit.Builder()
    .baseUrl(POKE_API_URL)
    .addConverterFactory(MoshiConverterFactory.create())
    .build()

val createPokedexApiService: PokedexApiService by lazy {
    retrofit.create(PokedexApiService::class.java)
}

//Room
private lateinit var INSTANCE: PokemonDatabase

fun getDatabase(context: Context): PokemonDatabase {
    synchronized(PokemonDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                PokemonDatabase::class.java,
                "pokemons"
            ).build()
        }
    }
    return INSTANCE
}