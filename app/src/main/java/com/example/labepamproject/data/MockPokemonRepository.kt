//package com.example.labepamproject.data
//
//import com.example.labepamproject.domain.GenerationEntity
//import com.example.labepamproject.domain.PokemonEntity
//import com.example.labepamproject.domain.PokemonRepository
//import io.reactivex.Single
//
//class MockPokemonRepository : PokemonRepository {
//
//    private val pokemonList = listOf(
//        PokemonEntity(1,"bigpokemonname", generateUrlFromId(1)),
//        PokemonEntity(2,"small", generateUrlFromId(2)),
//        PokemonEntity(3,"mediumname", generateUrlFromId(3)),
//        PokemonEntity(4,"4", generateUrlFromId(4)),
//        PokemonEntity(5,"5", generateUrlFromId(5)),
//        PokemonEntity(6,"6", generateUrlFromId(6)),
//        PokemonEntity(7,"7", generateUrlFromId(7)),
//        PokemonEntity(8,"8", generateUrlFromId(8)),
//        PokemonEntity(9,"9", generateUrlFromId(9)),
//        PokemonEntity(10,"10", generateUrlFromId(10)),
//        PokemonEntity(11,"11", generateUrlFromId(11)),
//        PokemonEntity(12,"12", generateUrlFromId(12)),
//        PokemonEntity(13,"12", generateUrlFromId(13)),
//        PokemonEntity(14,"14", generateUrlFromId(14)),
//        PokemonEntity(15,"15", generateUrlFromId(15)),
//    )
//
//    override fun getPokemons(): Single<List<PokemonEntity>> = Single.just(pokemonList)
//
//    override fun getPokemonByName(name: String): Single<PokemonEntity> {
//        TODO("Not yet implemented")
//    }
//
//    override fun getGenerations(): Single<List<GenerationEntity>> {
//        TODO("Not yet implemented")
//    }
//
//    private fun generateUrlFromId(id: Int): String = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
//}