package de.yanos.islam.util.constants

import androidx.annotation.StringRes
import de.yanos.islam.R

enum class Method(val id: Int, @StringRes val res: Int) {
    Shia(id = 0, res = R.string.method_shia),
    Karachi(id = 1, res = R.string.method_karachi),
    NorthAmerica(id = 2, res = R.string.method_north_america),
    WorldLeague(id = 3, res = R.string.method_world_league),
    Makkah(id = 4, res = R.string.method_makkah),
    Egypt(id = 5, res = R.string.method_egyptian),
    Tehran(id = 7, res = R.string.method_tehran),
    Gulf(id = 8, res = R.string.method_gulf),
    Kuwait(id = 9, res = R.string.method_kuwait),
    Qatar(id = 10, res = R.string.method_qatar),
    Singapore(id = 11, res = R.string.method_singapore),
    France(id = 12, res = R.string.method_france),
    Diyanet(id = 13, res = R.string.method_diyanet),
    Russia(id = 14, res = R.string.method_russia);

    companion object {
        fun valueFromId(methodId: Int): Method {
            return Method.entries.find { it.id == methodId } ?: Diyanet
        }
    }
}