package de.yanos.islam

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.annotation.RawRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.database.IslamDatabase
import de.yanos.islam.data.model.Quiz
import de.yanos.islam.data.model.Topic
import de.yanos.islam.data.model.TopicResource
import de.yanos.islam.data.model.TopicType
import de.yanos.islam.data.repositories.AwqatRepository
import de.yanos.islam.util.AppSettings
import de.yanos.islam.util.LatandLong
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDate
import javax.inject.Inject


@SuppressLint("StaticFieldLeak")
@HiltViewModel
class MainViewModel @Inject constructor(
    private val appSettings: AppSettings,
    private val geocoder: Geocoder,
    private val repository: AwqatRepository,
    @ApplicationContext private val context: Context,
    private val db: IslamDatabase,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) : ViewModel() {
    var isReady: Boolean = false

    init {
        initDB()
        loadDailyAwqatList()
    }

    fun onCurrentLocationChanged(location: LatandLong) {
        viewModelScope.launch {
            @Suppress("DEPRECATION")
            geocoder.getFromLocation(location.latitude, location.longitude, 1)?.firstOrNull()?.let { address ->
                (address.subAdminArea ?: address.adminArea)?.let { name ->
                    repository.fetchCityData(name)
                }
            }
        }
    }

    private fun loadDailyAwqatList() {
        viewModelScope.launch(dispatcher) {
            if (LocalDate.now().isAfter(LocalDate.ofEpochDay(appSettings.awqatLastFetch))) {
                repository.fetchAwqatData()
                appSettings.awqatLastFetch = LocalDate.now().toEpochDay()
            }
        }
    }

    private fun initDB() {
        viewModelScope.launch(dispatcher) {
            if (!appSettings.isDBInitialized) {
                TopicResource.values().forEach { topic ->
                    topic.raw?.let { raw ->
                        createQuizByTopic(raw, topic.id)
                    }
                    db.topicDao().insert(
                        Topic(
                            id = topic.id, title = topic.title, ordinal = topic.ordinal, parentId = topic.parent, type = when {
                                topic.parent == null && topic.raw != null -> TopicType.MAIN
                                topic.parent == null -> TopicType.GROUP
                                else -> TopicType.SUB
                            }
                        )
                    )
                }
                appSettings.isDBInitialized = true
            }
            delay(1200L)
            isReady = true
        }
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
                            val text = it.trim()
                            if (text.isNotBlank())
                                when {
                                    !it.matches(Regex("""\d+""")) || builder.isEmpty() -> builder.append(" $text")
                                    else -> builder.append("\n$text ")
                                }
                        }
                        db.quizDao().insert(Quiz(question = question, answer = builder.toString().replace("  ", " ").trim(), topicId = topicId, difficulty = 0))
                    }
                } catch (e: Exception) {
                    Timber.e(actualLine)
                    e.localizedMessage?.let { Timber.e("e", it) }
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