package de.yanos.islam.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import de.yanos.islam.data.model.quran.Ayah
import de.yanos.islam.data.model.quran.Surah
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranDao : BaseDao<Ayah> {
    @Query("SELECT * FROM Ayah ORDER BY id")
    suspend fun ayahs(): List<Ayah>

    @Query("SELECT * FROM Ayah WHERE (sureName LIKE '%' || :query || '%' OR translationTr LIKE '%' || :query || '%' OR transliterationEn LIKE '%' || :query || '%') ORDER BY sureId")
    fun findMatches(query: String): List<Ayah>

    @Query("SELECT * FROM Ayah WHERE sureId = :sureId ORDER BY id")
    fun loadSurah(sureId: Int): Flow<List<Ayah>>

    @Query("SELECT * FROM Surah ORDER BY id")
    fun sureList(): List<Surah>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSure(surah: Surah)

    @Transaction
    suspend fun insertSure(surah: Surah, ayahs: List<Ayah>) {
        insertSure(surah)
        insert(ayahs)
    }
}