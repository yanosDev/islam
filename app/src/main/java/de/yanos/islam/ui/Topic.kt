@file:OptIn(ExperimentalFoundationApi::class)

package de.yanos.islam.ui

import androidx.annotation.RawRes
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
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import de.yanos.islam.R
import de.yanos.islam.data.model.Topic
import de.yanos.islam.util.PatternedBackgroung
import de.yanos.islam.util.goldColor

@Preview
@Composable
fun TopicView(
    modifier: Modifier = Modifier,
    vm: TopicViewModel = hiltViewModel(),
    onTopicSelected: (String) -> Unit = {}
) {
    PatternedBackgroung(modifier = modifier) {
        Column {
            HeaderStars()
            TopicHeader(modifier = Modifier.align(Alignment.CenterHorizontally))
            TopicList(modifier = Modifier.align(Alignment.CenterHorizontally), topics = vm.state)
        }
    }
}

@Composable
fun HeaderStars(modifier: Modifier = Modifier) {
    PrimaryLottie(modifier = Modifier.height(220.dp), resId = R.raw.stars_moving)
}

@Composable
fun PrimaryLottie(modifier: Modifier, @RawRes resId: Int) {
    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR_FILTER,
            value = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                MaterialTheme.colorScheme.primary.hashCode(),
                BlendModeCompat.SRC_ATOP
            ),
            keyPath = arrayOf(
                "**"
            )
        )
    )
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(resId))
    val progress by animateLottieCompositionAsState(composition)
    LottieAnimation(
        modifier = modifier,
        composition = composition,
        progress = { progress },
        dynamicProperties = dynamicProperties
    )
}

@Composable
fun TopicHeader(modifier: Modifier) {
    Text(
        modifier = modifier,
        text = stringResource(id = R.string.topics_title),
        style = MaterialTheme.typography.displayLarge
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
                shape = CutCornerShape(8.dp),
                border = BorderStroke(1.dp, goldColor),
                onClick = {},
            ) {
                Text(text = topic.title, style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}
