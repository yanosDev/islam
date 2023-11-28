package de.yanos.islam.ui.ai

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AIScreenViewModel @Inject constructor(
    private val ai: OpenAI,
    @IODispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    internal var conversation = mutableStateListOf<UserRequest>()

    var requestInProgress by mutableStateOf(false)

    fun sendRequest(text: String) {
        requestInProgress = true
        viewModelScope.launch {
            val completion = withContext(dispatcher) {
                ai.chatCompletion(
                    ChatCompletionRequest(
                        model = ModelId("gpt-3.5-turbo-16k"),
                        listOf(ChatMessage(role = ChatRole.User, content = text))
                    )
                )
            }
            conversation.add(0, UserRequest(text = text, replyList = completion.choices.map { AssistantReply(text = it.message.content ?: "") }))
            requestInProgress = false
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}

data class UserRequest(val id: UUID = UUID.randomUUID(), val text: String, val replyList: List<AssistantReply>)
data class AssistantReply(val id: UUID = UUID.randomUUID(), val text: String)