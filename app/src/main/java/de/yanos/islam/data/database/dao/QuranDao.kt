package de.yanos.islam.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.yanos.islam.data.model.quran.Ayet
import de.yanos.islam.data.model.tanzil.SureDetail
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranDao : BaseDao<SureDetail> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSure(ayet: List<Ayet>)

    @Query("SELECT * FROM Ayet WHERE (sureaditr LIKE '%' || :query || '%' OR suretur LIKE '%' || :query || '%' OR suretrans LIKE '%' || :query || '%') ORDER BY sureOrdinal, ayetNr")
    fun findMatches(query: String): List<Ayet>

    @Query("SELECT * FROM Ayet WHERE sureaditr = :sureName ORDER BY ayetNr")
    fun loadSure(sureName: String): Flow<List<Ayet>>
}