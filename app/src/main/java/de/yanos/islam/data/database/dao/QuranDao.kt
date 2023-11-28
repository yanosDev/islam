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
    fun subscribeAyahs(): Flow<List<Ayah>>

    @Query("SELECT * FROM Ayah WHERE (sureName LIKE '%' || :query || '%' OR translationTr LIKE '%' || :query || '%' OR transliterationEn LIKE '%' || :query || '%') ORDER BY sureId")
    fun findMatches(query: String): List<Ayah>

    @Query("SELECT * FROM Ayah WHERE sureId = :sureId ORDER BY id")
    fun subsribeSurahAyahs(sureId: Int): Flow<List<Ayah>>

    @Query("SELECT * FROM Ayah WHERE id = :ayahId")
    fun subscribeAyah(ayahId: Int): Flow<Ayah>

    @Query("SELECT * FROM Ayah WHERE id = :ayahId")
    suspend fun loadAyah(ayahId: Int): Ayah?

    @Query("SELECT * FROM Ayah WHERE sureId = :surahId AND number = '1' ")
    suspend fun loadFirstAyahBySurahId(surahId: Int): Ayah?

    @Query("SELECT * FROM Ayah WHERE page = :page ORDER BY id LIMIT 1")
    suspend fun loadFirstAyahByPageId(page: Int): Ayah?

    @Query("SELECT * FROM Ayah WHERE juz = :juz ORDER BY id LIMIT 1")
    suspend fun loadFirstAyahByJuz(juz: Int): Ayah?

    @Query("SELECT * FROM Surah ORDER BY id")
    fun sureList(): List<Surah>

    @Query("SELECT * FROM Ayah ORDER BY id")
    fun ayahList(): List<Ayah>

    @Query("UPDATE Ayah SET localAudio = :localAudio WHERE id = :id")
    fun updateLocalAudio(id: Int, localAudio: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSure(surah: Surah)

    @Transaction
    suspend fun insertSure(surah: Surah, ayahs: List<Ayah>) {
        insertSure(surah)
        insert(ayahs)
    }

    @Query("SELECT COUNT(*) FROM Ayah")
    suspend fun ayahSize(): Int
}