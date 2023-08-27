package de.yanos.islam.ui.questions.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.R
import de.yanos.islam.data.model.Topic
import de.yanos.islam.data.model.TopicType
import de.yanos.islam.util.NavigationPath
import de.yanos.islam.util.Lottie
import de.yanos.islam.util.goldColor
import de.yanos.islam.util.headlineLarge
import de.yanos.islam.util.labelMedium
import de.yanos.islam.util.titleLarge

@Composable
fun MainTopicsScreen(
    modifier: Modifier = Modifier,
    vm: MainTopicViewModel = hiltViewModel(),
    onNavigationChange: (NavigationPath) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Lottie(modifier = modifier.height(220.dp), resId = R.raw.lottie_stars_moving)
        Text(modifier = Modifier.padding(bottom = 32.dp), text = stringResource(id = R.string.main_topic_title), style = headlineLarge())
        TopicButtons(topics = vm.list.collectAsState(initial = listOf()).value) { topic ->
            onNavigationChange(
                if (topic.type == TopicType.GROUP)
                    NavigationPath.NavigateToSubTopic(topic.id)
                else NavigationPath.NavigateToTopicQuestions(topic.id)
            )
        }
    }
}

@Composable
fun TopicButtons(
    modifier: Modifier = Modifier,
    topics: List<Topic>,
    onTopicClick: (Topic) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .widthIn(320.dp, 600.dp)
            .padding(horizontal = 32.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(
            items = topics,
            key = { it.id }
        ) { topic ->
            TopicButton(
                topic = topic,
                onTopicClick = onTopicClick
            )
        }
    }
}

@Composable
private fun TopicButton(
    modifier: Modifier = Modifier,
    topic: Topic,
    onTopicClick: (Topic) -> Unit
) {
    ElevatedButton(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        shape = AbsoluteCutCornerShape(8.dp),
        border = BorderStroke(1.dp, goldColor()),
        onClick = { onTopicClick(topic) },
    ) {
        Text(text = topic.title, style = labelMedium())
    }
}
