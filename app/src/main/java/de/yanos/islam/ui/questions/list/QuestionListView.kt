package de.yanos.islam.ui.questions.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.data.model.Quiz
import de.yanos.islam.util.IslamDivider
import de.yanos.islam.util.NavigationPath
import de.yanos.islam.util.bodyMedium
import de.yanos.islam.util.bodySmall
import de.yanos.islam.util.labelLarge
import de.yanos.islam.util.titleLarge

@Composable
fun QuestionListScreen(
    modifier: Modifier = Modifier,
    vm: QuestionListViewModel = hiltViewModel()
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(modifier = Modifier.padding(vertical = 32.dp), text = vm.topicName.collectAsState(initial = listOf()).value.firstOrNull() ?: "", style = titleLarge())
        Questions(questions = vm.list.collectAsState(initial = listOf()).value)
    }
}

@Composable
private fun Questions(
    modifier: Modifier = Modifier,
    questions: List<Quiz>
) {
    LazyColumn(
        modifier = modifier
            .wrapContentSize()
            .widthIn(320.dp, 800.dp)
            .padding(start = 32.dp, end = 32.dp, top = 8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(
            items = questions,
            key = { it.id }) { quiz ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(modifier = Modifier.padding(horizontal = 8.dp), text = "${quiz.question}?", style = labelLarge())
                Spacer(modifier = Modifier.height(2.dp))
                IslamDivider()
                Spacer(modifier = Modifier.height(2.dp))
                Text(modifier = Modifier.padding(horizontal = 8.dp), text = quiz.answer, style = bodyMedium())
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}