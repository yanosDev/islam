package de.yanos.islam.data.repositories

import de.yanos.islam.data.model.quran.Ayet
import de.yanos.islam.data.repositories.source.LocalQuranSource
import de.yanos.islam.data.repositories.source.RemoteQuranSource
import de.yanos.islam.util.getData
import timber.log.Timber
import javax.inject.Inject


interface QuranRepository {
    suspend fun fetchQuran()
}

class QuranRepositoryImpl @Inject constructor(
    private val local: LocalQuranSource,
    private val remote: RemoteQuranSource
) : QuranRepository {

    override suspend fun fetchQuran() {
        getData(remote.loadQuranSummary())?.let { response ->
            local.saveQuranSummary(response.kuran)
            response.links.forEach { (key, _) ->
                getData(remote.loadSure(key))?.let { (sureaditr, sureList) ->
                    Timber.e("Loaded $sureaditr")
                    local.saveSure(sureList.map { sure ->
                        Ayet(
                            id = "${key}_${sure.ayetID}",
                            sureOrdinal = key.toInt(),
                            sureaditr = sureaditr.trim(),
                            ayetNr = sure.ayetID,
                            surear = sure.surear.trim(),
                            suretrans = sure.suretrans.trim(),
                            suretur = sure.suretur.trim(),
                            sureen = sure.sureen.trim(),
                        )
                    })
                }
            }
        }
    }

}