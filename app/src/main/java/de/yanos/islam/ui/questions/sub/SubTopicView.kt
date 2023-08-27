package de.yanos.islam.ui.questions.sub

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.R
import de.yanos.islam.ui.questions.main.TopicButtons
import de.yanos.islam.util.NavigationPath
import de.yanos.islam.util.Lottie
import de.yanos.islam.util.headlineMedium
import de.yanos.islam.util.titleLarge

@Composable
fun SubTopicsScreen(
    modifier: Modifier = Modifier,
    vm: SubTopicViewModel = hiltViewModel(),
    onNavigationChange: (NavigationPath) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Lottie(modifier = Modifier.height(180.dp), resId = R.raw.lottie_girl_thinking, applyColor = false)
        Text(modifier = Modifier.padding(bottom = 32.dp), text = vm.topicName.collectAsState(initial = listOf()).value.firstOrNull() ?: "", style = headlineMedium())
        TopicButtons(topics = vm.list.collectAsState(initial = listOf()).value) { topic -> onNavigationChange(NavigationPath.NavigateToTopicQuestions(topic.id, topic.parentId)) }
    }
}