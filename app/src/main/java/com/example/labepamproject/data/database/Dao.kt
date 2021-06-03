package com.example.labepamproject.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PokemonDetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg pokemonDetails: PokemonDetailDatabaseEntity)

    @Query("select * from pokemondetaildatabaseentity where name like :name limit 1")
    suspend fun getPokemonByName(name: String): PokemonDetailDatabaseEntity
}

@Dao
interface PokemonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg pokemonNames: PokemonDatabaseEntity)

    @Query("select * from pokemondatabaseentity")
    suspend fun getPokemons(): List<PokemonDatabaseEntity>
}

@Dao
interface GenerationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg generations: GenerationDatabaseEntity)

    @Query("select * from generationdatabaseentity where id = :id limit 1")
    suspend fun getGenerationById(id: Int): GenerationDatabaseEntity

    @Query("select * from generationdatabaseentity order by id")
    suspend fun getGenerations(): List<GenerationDatabaseEntity>
}

