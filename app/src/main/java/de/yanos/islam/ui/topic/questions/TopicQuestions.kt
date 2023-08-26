package de.yanos.islam.ui.topic.questions

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.data.model.Quiz
import de.yanos.islam.util.IslamDivider
import de.yanos.islam.util.PatternedBackgroung

@Composable
@Preview
fun TopicContentView(
    modifier: Modifier = Modifier,
    vm: TopicQuestionsViewModel = hiltViewModel(),
    topicId: Int = 0
) {
    vm.loadTopicContent(topicId)
    PatternedBackgroung(modifier = modifier) {
        QuizList(quizList = vm.state)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun QuizList(modifier: Modifier = Modifier, quizList: List<Quiz>) {
    LazyColumn(
        modifier = modifier
            .wrapContentSize()
            .widthIn(320.dp, 800.dp)
            .padding(start = 32.dp, end = 32.dp, top = 8.dp)
    ) {
        items(
            items = quizList,
            key = { it.id }) { quiz ->
            ElevatedCard(
                modifier = Modifier
                    .animateItemPlacement()
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(modifier = Modifier.padding(horizontal = 8.dp), text = "${quiz.question}?", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(2.dp))
                IslamDivider()
                Spacer(modifier = Modifier.height(2.dp))
                Text(modifier = Modifier.padding(horizontal = 8.dp), text = quiz.answer, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}

@Composable
private fun ContentHeader(modifier: Modifier, title: String) {
    Text(
        modifier = modifier,
        text = title,
        style = MaterialTheme.typography.displayLarge
    )
}
