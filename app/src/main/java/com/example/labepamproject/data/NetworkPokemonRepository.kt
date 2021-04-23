package com.example.labepamproject.data

import com.example.labepamproject.data.network.PokedexApiService
import com.example.labepamproject.domain.Generation
import com.example.labepamproject.domain.Pokemon
import com.example.labepamproject.domain.PokemonRepository
import io.reactivex.Observable
import io.reactivex.Single

class NetworkPokemonRepository(val api: PokedexApiService) : PokemonRepository {
    override fun getPokemons(): Single<List<Pokemon>> {
        return api.fetchPokemonList()
            .flatMap { pokemonList ->
                Observable.fromIterable(pokemonList.results)
                    .flatMapSingle {
                        getPokemonByName(it.name)
                    }.toList()
            }
    }

    override fun getPokemonByName(name: String): Single<Pokemon> {
        return api.fetchPokemonInfo(name)
            .map {
                Pokemon(
                    id = it.id,
                    name = it.name,
                    prevImgUrl = generateUrlFromId(it.id),
                    experience = it.experience,
                    height = it.height,
                    weight = it.weight,
                )
            }
    }

    override fun getGenerations(): Single<List<Generation>> {
        return api.fetchGenerationList()
            .map { listResponse -> listResponse.results.map { Generation(it.name) } }
    }

    private fun generateUrlFromId(id: Int): String =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
}