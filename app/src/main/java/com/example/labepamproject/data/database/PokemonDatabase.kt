package com.example.labepamproject.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        PokemonDetailDatabaseEntity::class,
        PokemonDatabaseEntity::class,
        GenerationDatabaseEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PokemonDatabase : RoomDatabase() {
    abstract val pokemonDao: PokemonDao
    abstract val pokemonDetailDao: PokemonDetailDao
    abstract val generationDao: GenerationDao
}

