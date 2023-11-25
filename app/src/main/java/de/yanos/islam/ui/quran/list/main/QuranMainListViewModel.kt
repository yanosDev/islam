package de.yanos.islam.ui.quran.list.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.islam.data.database.dao.QuranDao
import de.yanos.islam.util.AppSettings
import javax.inject.Inject

@HiltViewModel
class QuranMainListViewModel @Inject constructor(
    private val appSettings: AppSettings,
    private val dao: QuranDao,
) : ViewModel() {
    val lastSurah = dao.subsribeSurahAyahs(114)
    val isDBInitialized = appSettings.isDBInitialized
}