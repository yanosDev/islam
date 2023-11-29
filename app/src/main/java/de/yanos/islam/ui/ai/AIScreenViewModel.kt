package de.yanos.islam.ui.ai

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.core.utils.IODispatcher
import de.yanos.core.utils.MainDispatcher
import de.yanos.islam.data.database.dao.BotDao
import de.yanos.islam.data.model.BotReply
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AIScreenViewModel @Inject constructor(
    private val ai: OpenAI,
    private val botDao: BotDao,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
) : ViewModel() {
    internal var conversation = botDao.loadPreviousQuestions()
    var requestInProgress by mutableStateOf(false)

    fun sendRequest(text: String) {
        requestInProgress = true
        viewModelScope.launch(dispatcher) {
            val completion = try {
                ai.chatCompletion(
                    ChatCompletionRequest(
                        model = ModelId("gpt-3.5-turbo-16k"),
                        listOf(ChatMessage(role = ChatRole.User, content = text))
                    )
                ).choices.map { it.message.content.toString() }
            } catch (e: Exception) {
                Timber.e(e)
                listOf("Malesef yardimci olamicam")
            }
            botDao.insert(BotReply(question = text, replies = completion.flatMap { it.split("\n\n").reversed() }.map { it.trim() }, ts = System.currentTimeMillis()))
            withContext(mainDispatcher) {
                requestInProgress = false
            }
        }
    }
}