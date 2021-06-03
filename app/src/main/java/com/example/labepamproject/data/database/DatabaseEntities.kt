package com.example.labepamproject.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.labepamproject.domain.entity.GenerationEntity
import com.example.labepamproject.domain.entity.PokemonEntity
import com.example.labepamproject.presentation.generateUrlFromId


@Entity
@TypeConverters(EntityTypeConverter::class)
data class PokemonDetailDatabaseEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val experience: Float,
    val height: Int,
    val weight: Int,
    val abilities: List<String>,
    val stats: List<Pair<String, Float>>,
    val types: List<String>,
)

@Entity
data class PokemonDatabaseEntity(
    @PrimaryKey
    val name: String
)

@Entity
@TypeConverters(EntityTypeConverter::class)
data class GenerationDatabaseEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val pokemons: List<String>
)

object EntityTypeConverter {

    @TypeConverter
    fun fromStringList(abilities: List<String>): String {
        return abilities.joinToString()
    }

    @TypeConverter
    fun toStringList(data: String): List<String> {
        return data.split(", ")
    }

    @TypeConverter
    fun fromStats(stats: List<Pair<String, Float>>): String {
        return stats.joinToString { "${it.first}:${it.second}" }
    }

    @TypeConverter
    fun toStats(data: String): List<Pair<String, Float>> {
        return data.split(", ").map {
            val pair = it.split(":")
            pair[0].trim() to pair[1].toFloat()
        }
    }
}

fun PokemonDetailDatabaseEntity.asEntity() =
    PokemonEntity(
        id = id,
        name = name,
        prevImgUrl = generateUrlFromId(id),
        experience = experience,
        height = height,
        weight = weight,
        abilities = abilities,
        stats = stats,
        types = types,
    )

fun PokemonEntity.asDatabaseEntity() =
    PokemonDetailDatabaseEntity(
        id = id,
        name = name,
        experience = experience,
        height = height,
        weight = weight,
        abilities = abilities,
        stats = stats,
        types = types,
    )

fun GenerationDatabaseEntity.asEntity() =
    GenerationEntity(
        id = id, name = name, pokemons = pokemons
    )

fun GenerationEntity.asDatabaseEntity() =
    GenerationDatabaseEntity(
        id = id, name = name, pokemons = pokemons
    )
