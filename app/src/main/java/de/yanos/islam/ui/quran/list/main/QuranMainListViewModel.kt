package de.yanos.islam.ui.quran.list.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.islam.data.database.dao.QuranDao
import javax.inject.Inject

@HiltViewModel
class QuranMainListViewModel @Inject constructor(
    private val dao: QuranDao,
) : ViewModel() {
    val lastSurah = dao.subscribeSurahAyahs(114)
}