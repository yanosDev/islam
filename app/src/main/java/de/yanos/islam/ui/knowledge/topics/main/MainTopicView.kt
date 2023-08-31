package de.yanos.islam.ui.knowledge.topics.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.NewLabel
import androidx.compose.material.icons.rounded.Preview
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
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
import de.yanos.islam.util.errorColor
import de.yanos.islam.util.goldColor
import de.yanos.islam.util.headlineLarge
import de.yanos.islam.util.labelMedium

@Composable
fun MainTopicsScreen(
    modifier: Modifier = Modifier,
    vm: MainTopicViewModel = hiltViewModel(),
    onNavigationChange: (NavigationAction) -> Unit
) {
    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
        item {
            Lottie(modifier = modifier.height(220.dp), resId = R.raw.lottie_stars_moving)
        }
        item {
            Text(modifier = Modifier.padding(bottom = 32.dp), text = stringResource(id = R.string.main_topic_title), style = headlineLarge())
        }
        item {
            TopicButtons(topics = vm.list.collectAsState(initial = listOf()).value) { topic ->
                onNavigationChange(
                    if (topic.type == TopicType.GROUP)
                        KnowledgeNavigationAction.NavigateToSubTopic(topic.id)
                    else KnowledgeNavigationAction.NavigateToTopicQuestions(topic.id, null)
                )
            }
        }
        item {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { onNavigationChange(KnowledgeNavigationAction.NavigateToOpenChallenges) }) {
                    Row {
                        Icon(imageVector = Icons.Rounded.Preview, contentDescription = "Previews Challenges", tint = errorColor())
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(id = R.string.main_to_open_challenges),
                            style = labelMedium(),
                        )
                    }
                }
                TextButton(onClick = { onNavigationChange(KnowledgeNavigationAction.NavigateToChallengeCreation) }) {
                    Row {
                        Icon(imageVector = Icons.Rounded.Quiz, contentDescription = "To Challenges")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = stringResource(id = R.string.main_to_new_challenge), style = labelMedium())
                    }
                }
            }
        }
    }
}

@Composable
fun TopicButtons(
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
