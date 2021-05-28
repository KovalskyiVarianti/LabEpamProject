package com.example.labepamproject.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.labepamproject.domain.PokemonEntity
import com.example.labepamproject.presentation.generateUrlFromId


@Entity
@TypeConverters(PokemonTypeConverter::class)
data class PokemonDatabaseEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val experience: Int,
    val height: Int,
    val weight: Int,
    val abilities: List<String>,
    val stats: List<Pair<String, Float>>,
    val types: List<String>,
)

fun PokemonDatabaseEntity.asEntity() =
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
    PokemonDatabaseEntity(
        id = id,
        name = name,
        experience = experience,
        height = height,
        weight = weight,
        abilities = abilities,
        stats = stats,
        types = types,
    )

object PokemonTypeConverter {

    @TypeConverter
    fun fromAbilitiesOrTypes(abilities: List<String>): String {
        return abilities.joinToString()
    }

    @TypeConverter
    fun toAbilitiesOrTypes(data: String): List<String> {
        return data.split(",")
    }

    @TypeConverter
    fun fromStats(stats: List<Pair<String, Float>>): String {
        return stats.joinToString { "${it.first}:${it.second}" }
    }

    @TypeConverter
    fun toStats(data: String): List<Pair<String, Float>> {
        return data.split(",").map {
            val pair = it.split(":")
            pair[0].trim() to pair[1].toFloat()
        }
    }
}
