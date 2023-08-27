package de.yanos.islam.ui.challenge.open

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.database.dao.ChallengeDao
import de.yanos.islam.data.database.dao.TopicDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OpenChallengesViewModel @Inject constructor(
    private val challengeDao: ChallengeDao,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    private val topicDao: TopicDao
) : ViewModel() {
    val challenges = challengeDao.openChallenges()

    fun deleteAllOpenChallenges() {
        viewModelScope.launch {
            withContext(ioDispatcher) {
                challengeDao.deleteAllOpenChallenges()
            }
        }
    }

    fun deleteOpenChallenge(id: Int) {
        viewModelScope.launch(ioDispatcher) {
            withContext(ioDispatcher) {
                challengeDao.deleteById(id)
            }
        }
    }
}

data class OpenChallenge(
    val id: Int,
    val count: String,
    val corrects: String,
    val failures: String,
    val topics: String
)