@file:UnstableApi
@file:OptIn(ExperimentalFoundationApi::class)

package de.yanos.islam.ui.quran.learning

import android.content.pm.ActivityInfo
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import de.yanos.islam.util.NavigationAction
import de.yanos.islam.util.setScreenOrientation

@Composable
fun QuranLearningScreen(
    modifier: Modifier = Modifier,
    vm: QuranLearningViewModel = hiltViewModel(),
    onNavigationChange: (NavigationAction) -> Unit
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
                AndroidView(modifier = Modifier.fillMaxWidth().heightIn(min = 248.dp),
                    factory = { playerView })
            ) {
                onDispose {
                    playerView.player?.pause()
                }
            }
        }
        items(items = vm.learnings, key = { it.id }) { learning ->
            LearnItem(learning) { l ->
                vm.player.seekTo(vm.learnings.indexOf(l), 0)
            }
        }
    }
}

@Composable
fun LearnItem(learning: Learning, playItem: @Composable (learning: Learning) -> Unit) {
    Row(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        //https://developer.android.com/jetpack/compose/graphics/images/loading
        Column {
            Text(modifier = Modifier.padding(vertical = 4.dp), text = learning.title, style = MaterialTheme.typography.labelMedium)
            //Text(text = learning.subTitle)
            Text(modifier = Modifier.padding(vertical = 4.dp), text = learning.author, style = MaterialTheme.typography.labelSmall)
        }
    }
}
