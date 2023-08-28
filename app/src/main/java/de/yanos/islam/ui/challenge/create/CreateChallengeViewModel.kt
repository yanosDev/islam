package de.yanos.islam.ui.challenge.create

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.database.dao.ChallengeDao
import de.yanos.islam.data.database.dao.TopicDao
import de.yanos.islam.data.model.Challenge
import de.yanos.islam.data.model.TopicType
import de.yanos.islam.util.ChallengeDifficulty
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CreateChallengeViewModel @Inject constructor(
    private val challengeDao: ChallengeDao,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    private val topicDao: TopicDao,
) : ViewModel() {
    var hasOpenChallenges = challengeDao.hasOpenChallenges()
    var topics = mutableStateListOf<List<TopicSelection>>()
    var showCreationError by mutableStateOf(false)
    var difficulty by mutableStateOf<ChallengeDifficulty>(ChallengeDifficulty.Low)
    private var initialSelections = mutableListOf<TopicSelection>()

    init {
        viewModelScope.launch {
            topicDao.allAsSelection().collect { selections ->
                initialSelections.clear()
                initialSelections.addAll(selections)
                recreateTopics()
            }
        }
    }

    private fun recreateTopics() {
        var batch = mutableListOf<TopicSelection>()
        val newList = mutableListOf<List<TopicSelection>>()
        initialSelections.forEachIndexed { index, topicSelection ->
            when {
                index == 0 || index == initialSelections.size - 1 -> batch.add(topicSelection)
                initialSelections[index + 1].parentId == null -> batch.add(topicSelection)
                (topicSelection.parentId != null && (initialSelections[index - 1].parentId == topicSelection.parentId || initialSelections[index + 1].parentId == topicSelection.parentId)) -> batch.add(
                    topicSelection
                )

                else -> {
                    newList.add(batch)
                    batch = mutableListOf()
                    batch.add(topicSelection)
                }
            }
        }
        newList.add(batch)
        topics.clear()
        topics.addAll(newList)
    }

    fun createForm(navigateToChallenge: (Int) -> Unit) {
        val flatTopics = topics.flatten()
        if (flatTopics.any { it.parentId == null && it.isSelected }) {
            viewModelScope.launch(ioDispatcher) {
                challengeDao.insert(
                    Challenge(
                        topicIds = flatTopics.filter { selection ->
                            selection.isSelected
                                    && (selection.parentId == null
                                    || flatTopics.any { it.id == selection.parentId && it.isSelected }
                                    )
                        }.map { it.id },
                        lastAction = System.currentTimeMillis(),
                        quizCount = difficulty.quizCount,
                        quizDifficulty = difficulty.quizMinDifficulty,
                    )
                )
                val id = challengeDao.newestChallengeId()
                withContext(Dispatchers.Main) {
                    navigateToChallenge(id)
                }
            }
        } else showCreationError = true
    }

    fun updateSelection(id: Int, isSelected: Boolean) {
        viewModelScope.launch {
            initialSelections.find { it.id == id }?.isSelected = isSelected
            recreateTopics()
        }
    }
}

data class TopicSelection(
    val id: Int,
    val title: String,
    val ordinal: Int,
    val parentId: Int?,
    val type: TopicType,
    var isSelected: Boolean = true,
)