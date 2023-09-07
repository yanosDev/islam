package de.yanos.islam.ui.quran.list.sure

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.database.dao.QuranDao
import de.yanos.islam.data.model.tanzil.SureDetail
import de.yanos.islam.util.AppSettings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SureListViewModel @Inject constructor(
    private val appSettings: AppSettings,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val quranDao: QuranDao,
) : ViewModel() {
    var sureList = mutableStateListOf<SureDetail>()
    private var sortBy by mutableStateOf(SureSorting.values()[appSettings.sortByOrdinal])

    init {
        viewModelScope.launch {
            val list = withContext(dispatcher) { quranDao.sureList() }
            sureList.addAll(list)
            recreateList()
        }
    }

    private suspend fun recreateList() {
        viewModelScope.launch {
            val currentList = sureList.toList()
            sureList.clear()
            sureList.addAll(
                currentList.sortSure(sortBy)
            )
        }
    }
}
 fun List<SureDetail>.sortSure(sortBy: SureSorting): List<SureDetail> {
    return this.sortedBy {
        when (sortBy) {
            SureSorting.ORIGINAL -> it.kuransira.toInt()
            SureSorting.DESCENDENCE -> it.inissira.toInt()
            SureSorting.ALPHABETICAL -> it.alfabesira.toInt()
        }
    }
}

enum class SureSorting {
    ORIGINAL, DESCENDENCE, ALPHABETICAL
}
