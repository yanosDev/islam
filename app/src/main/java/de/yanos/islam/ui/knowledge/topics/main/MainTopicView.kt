@file:OptIn(ExperimentalFoundationApi::class)

package de.yanos.islam.ui.knowledge.topics.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.QuestionAnswer
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import de.yanos.islam.util.KnowledgeNavigationAction
import de.yanos.islam.util.Lottie
import de.yanos.islam.util.NavigationAction
import de.yanos.islam.util.goldColor
import de.yanos.islam.util.headlineLarge
import de.yanos.islam.util.labelMedium

@Composable
fun MainTopicsScreen(
    modifier: Modifier = Modifier,
    vm: MainTopicViewModel = hiltViewModel(),
    onNavigationChange: (NavigationAction) -> Unit
) {
    val topics = vm.list.collectAsState(initial = listOf()).value
    Column(
        modifier
            .fillMaxSize()
            .padding(bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Lottie(modifier = modifier.height(128.dp), resId = R.raw.lottie_stars_moving)
            }
            item {
                Text(text = stringResource(id = R.string.main_topic_title), style = headlineLarge())
            }
            item {
                TopicButtons(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 48.dp), topics = topics
                ) { topic ->
                    onNavigationChange(
                        if (topic.type == TopicType.GROUP)
                            KnowledgeNavigationAction.NavigateToSubTopic(topic.id)
                        else KnowledgeNavigationAction.NavigateToTopicQuestions(topic.id, null)
                    )
                }
            }
        }
        Column(modifier = Modifier, verticalArrangement = Arrangement.Bottom) {
            OutlinedButton(onClick = { onNavigationChange(KnowledgeNavigationAction.NavigateToSearchQuestions) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Rounded.Search, contentDescription = "To Challenges")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(id = R.string.main_search_question), style = labelMedium())
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { onNavigationChange(KnowledgeNavigationAction.NavigateToChallengeCreation) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Rounded.QuestionAnswer, contentDescription = "To Challenges")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(id = R.string.main_to_new_challenge), style = labelMedium())
                }
            }
        }
    }
}

@Composable
internal fun TopicButtons(
    modifier: Modifier = Modifier,
    topics: List<Topic>,
    onTopicClick: (Topic) -> Unit
) {
    Column(
        modifier = modifier
            .widthIn(320.dp, 600.dp)
            .padding(horizontal = 32.dp)
            .wrapContentHeight(),
    ) {
        topics.forEach { topic ->
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
