package de.yanos.islam.ui.knowledge.topics.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.islam.data.database.dao.TopicDao
import de.yanos.islam.util.settings.AppSettings
import javax.inject.Inject

@HiltViewModel
class MainTopicViewModel @Inject constructor(
    private val appSettings: AppSettings,
    private val dao: TopicDao
) : ViewModel() {
    val list = dao.allMain()
}