package de.yanos.islam

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.annotation.RawRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.database.IslamDatabase
import de.yanos.islam.data.model.Quiz
import de.yanos.islam.data.model.TopicResource
import de.yanos.islam.data.model.Topic
import de.yanos.islam.util.AppSettings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class MainViewModel @Inject constructor(
    private val appSettings: AppSettings,
    @ApplicationContext private val context: Context,
    private val db: IslamDatabase,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) : ViewModel() {
    var isReady: Boolean = false

    init {
        viewModelScope.launch(dispatcher) {
            if (!appSettings.isDBInitialized) {
                initDB()
            }
            delay(1200L)
            isReady = true
        }
    }

    private fun initDB() {
        TopicResource.values().forEach { topic ->
            topic.raw?.let { raw ->
                createQuizByTopic(raw, topic.id)
            }
            db.topicDao().insert(Topic(id = topic.id, title = topic.title, ordinal = topic.ordinal, parentTopicId = topic.parent, hasSubTopics = topic.raw == null))
        }
        appSettings.isDBInitialized = true
    }

    private fun createQuizByTopic(@RawRes topicRaw: Int, topicId: Int) {
        val regex = Regex("""\d+|\D+""")
        val inputStream: InputStream = context.resources.openRawResource(topicRaw)
        val br = BufferedReader(InputStreamReader(inputStream))
        val lines = br.readLines()
        lines.forEachIndexed { index, line ->
            if (line.trim().startsWith("*")) {
                val actualLine = mergeLines(lines, index)
                    .replace("*", "")
                    .replace("  ", " ")
                    .trim()
                try {
                    actualLine.split("?").let { (question, answer) ->
                        val parts = regex.findAll(answer).map { it.groupValues.first() }.toList()
                        val builder = StringBuilder()
                        parts.forEach {
                            val text = it.trim().replace(".", "")
                            if (text.isNotBlank())
                                if (it.matches(Regex("""\d+""")))
                                    builder.append("$text ")
                                else builder.append("$text\n")
                        }
                        db.quizDao().insert(Quiz(question = question, answer = builder.toString(), topicId = topicId, difficulty = 0))
                    }
                } catch (e: Exception) {
                    Log.e("e", actualLine)
                    e.localizedMessage?.let { Log.e("e", it) }
                }
            }
        }
    }

    private fun mergeLines(lines: List<String>, index: Int): String {
        val currentLine = lines[index]
        return if (index == lines.size - 1 || lines[index + 1].trim().startsWith("*")) {
            currentLine
        } else currentLine.trim() + " " + mergeLines(lines, index + 1).trim()
    }
}