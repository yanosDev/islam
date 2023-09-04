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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SureListViewModel @Inject constructor(
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val quranDao: QuranDao,
) : ViewModel() {
    var sureList = mutableStateListOf<SureDetail>()
    var sortBy by mutableStateOf(SureSorting.ORIGINAL)

    init {
        viewModelScope.launch {
            val list = withContext(dispatcher) { quranDao.sureList() }
            sureList.addAll(list)
            recreateList()
        }
    }

    private suspend fun recreateList() {
        viewModelScope.launch(dispatcher) {
            val currentList = sureList.toList()
            sureList.clear()
            sureList.addAll(
                currentList.sortedBy {
                    when (sortBy) {
                        SureSorting.ORIGINAL -> it.kuransira
                        SureSorting.DESCENDENCE -> it.inissira
                        SureSorting.ALPHABETICAL -> it.alfabesira
                    }
                }
            )
        }
    }
}

enum class SureSorting {
    ORIGINAL, DESCENDENCE, ALPHABETICAL
}
