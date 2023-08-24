@file:OptIn(ExperimentalFoundationApi::class)

package de.yanos.islam.ui
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import de.yanos.islam.R
import de.yanos.islam.data.model.Topic
import de.yanos.islam.util.PatternedBackgroung

@Preview
@Composable
fun TopicView(
    modifier: Modifier = Modifier,
    vm: TopicViewModel = hiltViewModel(),
    onTopicSelected: (String) -> Unit = {}
) {
    PatternedBackgroung(modifier = modifier) {
        Column(modifier = Modifier.padding(top = 32.dp)) {
            HeaderStars()
            TopicHeader(modifier = Modifier.align(Alignment.CenterHorizontally))
            TopicList(modifier = Modifier.align(Alignment.CenterHorizontally), topics = vm.state)
        }
    }
}
@Composable
fun HeaderStars(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.stars_moving))
    val progress by animateLottieCompositionAsState(composition)
    LottieAnimation(
        modifier = modifier.height(124.dp),
        composition = composition,
        progress = { progress },
    )
}
@Composable
fun TopicHeader(modifier: Modifier) {
    Text(
        modifier = modifier,
        text = stringResource(id = R.string.topics_title),
        style = MaterialTheme.typography.displaySmall
    )
}

@Composable
fun TopicList(modifier: Modifier = Modifier, topics: List<Topic>) {
    LazyColumn(
        modifier = modifier
            .wrapContentSize()
            .widthIn(320.dp, 600.dp)
            .padding(32.dp),
    ) {
        items(
            items = topics,
            key = { it.id }
        ) { topic ->
            ElevatedButton(
                modifier = Modifier
                    .animateItemPlacement()
                    .padding(vertical = 4.dp)
                    .fillMaxWidth(),
                shape = CutCornerShape(4.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                onClick = {},
            ) {
                Text(text = topic.title, style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}
