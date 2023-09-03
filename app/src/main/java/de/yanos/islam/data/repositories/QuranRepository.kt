package de.yanos.islam.data.repositories

import de.yanos.islam.data.model.Ayet
import de.yanos.islam.data.repositories.source.LocalQuranSource
import de.yanos.islam.data.repositories.source.RemoteQuranSource
import de.yanos.islam.util.getData
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
            response.links.forEach { (key, value) ->
                getData(remote.loadSure(key))?.let { (sureaditr, sureList) ->
                    local.saveSure(sureList.map { sure ->
                        Ayet(
                            id = "${key}_${sureaditr}",
                            sureaditr = sureaditr,
                            ayetNr = sure.ayetID,
                            surear = sure.surear,
                            suretrans = sure.suretrans,
                            suretur = sure.suretur,
                            sureen = sure.sureen,
                        )
                    })
                }
            }

        }
    }

}