package de.yanos.islam.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import de.yanos.islam.data.model.Ayet
import de.yanos.islam.data.model.tanzil.SureDetail

@Dao
interface QuranDao : BaseDao<SureDetail> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSure(ayet: List<Ayet>)
}