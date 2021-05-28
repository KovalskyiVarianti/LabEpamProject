package com.example.labepamproject.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PokemonDatabaseEntity::class], version = 1, exportSchema = false)
abstract class PokemonDatabase : RoomDatabase() {
    abstract val pokemonDao: PokemonDao
}

