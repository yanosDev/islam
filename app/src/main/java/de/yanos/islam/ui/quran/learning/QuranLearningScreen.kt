@file:UnstableApi
@file:OptIn(ExperimentalFoundationApi::class)

package de.yanos.islam.ui.quran.learning

import android.content.pm.ActivityInfo
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import de.yanos.islam.data.model.VideoLearning
import de.yanos.islam.util.helper.goldColor
import de.yanos.islam.util.helper.setScreenOrientation

@Composable
fun QuranLearningScreen(
    modifier: Modifier = Modifier,
    vm: QuranLearningViewModel = hiltViewModel()
) {
    val playerView = PlayerView(LocalContext.current).apply {
        useController = true
        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        player = vm.player
        artworkDisplayMode = PlayerView.ARTWORK_DISPLAY_MODE_FIT
        setFullscreenButtonClickListener { isFullScreen ->
            with(context) {
                if (isFullScreen) {
                    setScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                } else {
                    setScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                }
            }
        }
    }
    LazyColumn(modifier = modifier) {
        stickyHeader {
            DisposableEffect(
                AndroidView(modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1290F / 720F)
                    .heightIn(min = 248.dp),
                    factory = { playerView })
            ) {
                onDispose {
                    playerView.player?.pause()
                }
            }
        }
        item { Spacer(modifier = Modifier.size(10.dp)) }
        items(items = vm.learnings, key = { it.id }) { learning ->
            Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)) {
                LearnItem(learning = learning, vm.index == learning.index) {
                    vm.loadVideo(learning)
                }
            }
        }
    }
}

@Composable
fun LearnItem(learning: VideoLearning, isCurrentlySelected: Boolean, playItem: () -> Unit) {
    val shape = RoundedCornerShape(4.dp)
    val modifier = if (isCurrentlySelected)
        Modifier
            .clickable { playItem() }
            .clip(shape)
            .background(color = goldColor().copy(0.1f), shape = shape)
    else Modifier
        .clickable { playItem() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 128.dp),
        verticalAlignment = Alignment.Top
    ) {
        AsyncImage(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 4.dp)
                .clip(shape)
                .background(shape = shape, color = Color.Transparent)
                .height(78.dp)
                .width(96.dp),
            model = learning.thumbRemoteUrl,
            contentDescription = null,
        )
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .padding(vertical = 8.dp, horizontal = 4.dp), verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(modifier = Modifier.fillMaxWidth(), text = learning.title, style = MaterialTheme.typography.labelMedium)
            //Text(text = learning.subTitle)
            Text(modifier = Modifier.fillMaxWidth(), text = learning.author, style = MaterialTheme.typography.labelSmall)
        }
    }
}