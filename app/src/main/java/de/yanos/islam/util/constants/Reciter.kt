package de.yanos.islam.util.constants

import androidx.annotation.StringRes
import de.yanos.islam.R

enum class Reciter(val id: Int, @StringRes val res: Int, val edition: String) {
    Ajamy(id = 0, res = R.string.reciter_ajamy, edition ="ar.ahmedajamy"),
    Alafasi(id = 1, res = R.string.reciter_alafasi, edition ="ar.alafasy"),
    Husary(id = 2, res = R.string.reciter_husary, edition ="ar.husary"),
    Alhusary(id = 3, res = R.string.reciter_alhusary, edition ="ar.husarymujawwad"),
    Mahermuaiqly(id = 4, res = R.string.reciter_mahermuaiqly, edition ="ar.mahermuaiqly"),
    Muhammadayyoub(id = 5, res = R.string.reciter_muhammadayyoub, edition ="ar.muhammadayyoub")
}