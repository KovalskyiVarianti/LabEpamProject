package com.example.labepamproject.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PokemonDao {
    @Query("select * from pokemondatabaseentity order by id")
    fun getPokemons(): List<PokemonDatabaseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg pokemons: PokemonDatabaseEntity)

    @Query("select * from pokemondatabaseentity where name like :name limit 1")
    fun getPokemonByName(name: String): PokemonDatabaseEntity
}
