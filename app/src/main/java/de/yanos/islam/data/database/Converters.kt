package de.yanos.islam.data.database

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromListIntToString(intList: List<Int>): String = intList.map { it.toString() }.joinToString("::")

    @TypeConverter
    fun toListIntFromString(stringList: String): List<Int> {
        return stringList.takeIf { it.isNotEmpty() }?.split("::")?.map { it.toInt() } ?: listOf()
    }

    @TypeConverter
    fun fromListStringToString(intList: List<String>): String = intList.joinToString("::") { it }

    @TypeConverter
    fun toListStringFromString(stringList: String): List<String> {
        return stringList.takeIf { it.isNotEmpty() }?.split("::") ?: listOf()
    }
}