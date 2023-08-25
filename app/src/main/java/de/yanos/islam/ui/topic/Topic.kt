@file:OptIn(ExperimentalFoundationApi::class)

package de.yanos.islam.ui.topic

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.R
import de.yanos.islam.data.model.Topic
import de.yanos.islam.util.PatternedBackgroung
import de.yanos.islam.util.PrimaryLottie
import de.yanos.islam.util.goldColor

@Preview
@Composable
fun SubTopicView(
    modifier: Modifier = Modifier,
    topicId: Int = 0,
    vm: TopicViewModel = hiltViewModel(),
    onTopicClick: (Topic) -> Unit = {}
) {
    vm.loadSubTopic(topicId)
    PatternedBackgroung(modifier = modifier) {
        Column {
            HeaderStars()
            TopicHeader(modifier = Modifier.align(Alignment.CenterHorizontally), title = stringResource(id = R.string.topics_title))
            TopicList(modifier = Modifier.align(Alignment.CenterHorizontally), topics = vm.state, onTopicClick = onTopicClick)
        }
    }
}

@Preview
@Composable
fun TopicView(
    modifier: Modifier = Modifier,
    vm: TopicViewModel = hiltViewModel(),
    onOpenQuizByTopic: (Topic) -> Unit = {}
) {
    PatternedBackgroung(modifier = modifier) {
        Column {
            HeaderStars()
            TopicHeader(modifier = Modifier.align(Alignment.CenterHorizontally), title = stringResource(id = R.string.topics_title))
            TopicList(modifier = Modifier.align(Alignment.CenterHorizontally), topics = vm.state, onTopicClick = onOpenQuizByTopic)
        }
    }
}

@Composable
private fun HeaderStars(modifier: Modifier = Modifier) {
    PrimaryLottie(modifier = Modifier.height(220.dp), resId = R.raw.stars_moving)
}

@Composable
private fun TopicHeader(modifier: Modifier, title: String) {
    Text(
        modifier = modifier,
        text = title,
        style = MaterialTheme.typography.displayLarge
    )
}

@Composable
private fun TopicList(
    modifier: Modifier = Modifier,
    topics: List<Topic>,
    onTopicClick: (Topic) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .widthIn(320.dp, 600.dp)
            .padding(horizontal = 32.dp),
    ) {
        items(
            items = topics,
            key = { it.id }
        ) { topic ->
            ElevatedButton(
                modifier = Modifier
                    .animateItemPlacement()
                    .padding(vertical = 2.dp)
                    .fillMaxWidth(),
                shape = AbsoluteCutCornerShape(8.dp),
                border = BorderStroke(1.dp, goldColor.copy(alpha = 0.4f)),
                onClick = { onTopicClick(topic) },
            ) {
                Text(text = topic.title, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
