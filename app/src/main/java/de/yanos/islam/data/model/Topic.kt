package de.yanos.islam.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.yanos.islam.R

@Entity
data class Topic(
    @PrimaryKey val id: Int,
    val title: String,
    val ordinal: Int,
    val parentTopicId: Int?,
    val hasSubTopics: Boolean
)


enum class TopicResource(val id: Int, val title: String, val raw: Int? = null, val parent: Int? = null) {
    ITIKAT(id = 0, title = "Itikat/Iman"),
    IBADET(id = 1, title = "Ibadet"),
    HAYAT(id = 2, title = "Peygamber Efendimizin Hayati (Siyer-i Nebî)", raw = R.raw.hayat),
    AHLAK(id = 3, title = "Ahlak", raw = R.raw.ahlak),
    KULTUR(id = 4, title = "Genel Kültür", raw = R.raw.kultur_genel),
    FARZ(id = 5, title = "32 Farz", raw = R.raw.farz),
    IBADET_GENEL(id = 101, title = "Genel", raw = R.raw.ibadet_genel, parent = 1),
    IBADET_ABDEST(id = 100, title = "Abdest", raw = R.raw.ibadet_abdest, parent = 1),
    IBADET_HAC(id = 102, title = "Hac", raw = R.raw.ibadet_hac, parent = 1),
    IBADET_KURBAN(id = 103, title = "Kurban", raw = R.raw.ibadet_kurban, parent = 1),
    IBADET_NAMAZ(id = 104, title = "Namaz", raw = R.raw.ibadet_namaz, parent = 1),
    IBADET_ORUC(id = 105, title = "Oruc", raw = R.raw.ibadet_oruc, parent = 1),
    IBADET_ZEKAT(id = 106, title = "Zekat", raw = R.raw.ibadet_zekat, parent = 1),
    ITIKAT_GENEL(id = 107, title = "Genel", raw = R.raw.itikat_genel, parent = 0),
    IMAN_AHIRET(id = 108, title = "Ahiret", raw = R.raw.iman_ahiret, parent = 0),
    IMAN_ALLAH(id = 109, title = "Allah", raw = R.raw.iman_allah, parent = 0),
    IMAN_KADER(id = 110, title = "Kader", raw = R.raw.iman_kader, parent = 0),
    IMAN_MELEK(id = 111, title = "Melek", raw = R.raw.iman_melek, parent = 0),
    IMAN_PEYGAMBER(id = 112, title = "Peygamber", raw = R.raw.iman_peygamber, parent = 0),
}