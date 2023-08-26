package de.yanos.islam.data.database

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromListIntToString(intList: List<Int>): String = intList.map { it.toString() }.joinToString("::")

    @TypeConverter
    fun toListIntFromString(stringList: String): List<Int> {
        return stringList.takeIf { it.isNotEmpty() }?.split("::")?.map { it.toInt() } ?: listOf()
    }
}