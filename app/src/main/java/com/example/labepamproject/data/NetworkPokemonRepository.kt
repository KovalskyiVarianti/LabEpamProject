package com.example.labepamproject.data

import com.example.labepamproject.data.network.PokedexApiService
import com.example.labepamproject.domain.GenerationEntity
import com.example.labepamproject.domain.PokemonEntity
import com.example.labepamproject.domain.PokemonRepository
import io.reactivex.Observable
import io.reactivex.Single

class NetworkPokemonRepository(val api: PokedexApiService) : PokemonRepository {
    override fun getPokemons(): Single<List<PokemonEntity>> {
        return api.fetchPokemonList()
            .flatMap { pokemonList ->
                Observable.fromIterable(pokemonList.results)
                    .flatMapSingle {
                        getPokemonByName(it.name)
                    }.toList()
            }
    }

    override fun getPokemonByName(name: String): Single<PokemonEntity> {
        return api.fetchPokemonInfo(name)
            .map { pokemonResponse ->
                val abilities = pokemonResponse.abilities.map { it.ability.name }
                val stats = pokemonResponse.stats.map { it.stat.name to it.base_stat }
                val types = pokemonResponse.types.map { it.type.name }
                PokemonEntity(
                    id = pokemonResponse.id,
                    name = pokemonResponse.name,
                    prevImgUrl = generateUrlFromId(pokemonResponse.id),
                    experience = pokemonResponse.experience,
                    height = pokemonResponse.height,
                    weight = pokemonResponse.weight,
                    abilities = abilities,
                    stats = stats,
                    types = types,
                )
            }
    }

    override fun getGenerations(): Single<List<GenerationEntity>> {
        return api.fetchGenerationList()
            .map { listResponse -> listResponse.results.map { GenerationEntity(it.name) } }
    }

    private fun generateUrlFromId(id: Int): String =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
}