package de.yanos.islam.util.constants

import androidx.annotation.StringRes
import de.yanos.islam.R

enum class School(val id: Int, @StringRes val res: Int) {
    Shafi(id = 0, res = R.string.school_safi),
    Hanafi(id = 1, res = R.string.school_hanafi),
}