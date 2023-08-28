package de.yanos.islam.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.yanos.islam.R

@Entity
data class Topic(
    @PrimaryKey val id: Int,
    val title: String,
    val ordinal: Int,
    val parentId: Int?,
    val type: TopicType
)

enum class TopicType {
    MAIN, GROUP, SUB
}

enum class TopicResource(val id: Int, val title: String, val raw: Int? = null, val parent: Int? = null) {
    FARZ(id = 1, title = "32 Farz", raw = R.raw.farz),
    AHLAK(id = 2, title = "Ahlak", raw = R.raw.ahlak),
    KULTUR(id = 3, title = "Genel Kültür", raw = R.raw.kultur_genel),
    HAYAT(id = 6, title = "Peygamber Efendimizin Hayati (Siyer-i Nebî)", raw = R.raw.hayat),
    IBADET(id = 4, title = "Ibadet"),
    IBADET_ABDEST(id = 100, title = "Abdest", raw = R.raw.ibadet_abdest, parent = 4),
    IBADET_GENEL(id = 101, title = "Genel", raw = R.raw.ibadet_genel, parent = 4),
    IBADET_HAC(id = 102, title = "Hac", raw = R.raw.ibadet_hac, parent = 4),
    IBADET_KURBAN(id = 103, title = "Kurban", raw = R.raw.ibadet_kurban, parent = 4),
    IBADET_NAMAZ(id = 104, title = "Namaz", raw = R.raw.ibadet_namaz, parent = 4),
    IBADET_ORUC(id = 105, title = "Oruc", raw = R.raw.ibadet_oruc, parent = 4),
    IBADET_ZEKAT(id = 106, title = "Zekat", raw = R.raw.ibadet_zekat, parent = 4),
    ITIKAT(id = 5, title = "Itikat/Iman"),
    ITIKAT_GENEL(id = 107, title = "Genel", raw = R.raw.itikat_genel, parent = 5),
    IMAN_AHIRET(id = 108, title = "Ahiret", raw = R.raw.iman_ahiret, parent = 5),
    IMAN_ALLAH(id = 109, title = "Allah", raw = R.raw.iman_allah, parent = 5),
    IMAN_KADER(id = 110, title = "Kader", raw = R.raw.iman_kader, parent = 5),
    IMAN_MELEK(id = 111, title = "Melek", raw = R.raw.iman_melek, parent = 5),
    IMAN_PEYGAMBER(id = 112, title = "Peygamber", raw = R.raw.iman_peygamber, parent = 5),
}