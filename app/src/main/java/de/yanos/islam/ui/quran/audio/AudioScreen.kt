package de.yanos.islam.ui.quran.audio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import de.yanos.islam.ui.quran.classic.AudioEvents
import de.yanos.islam.ui.quran.classic.QuranClassicViewModel

@Composable
fun AyahAudioPlayer(
    modifier: Modifier,
    item: MediaItem?,
    isPlaying: Boolean,
    onAyahChange: (event: AudioEvents) -> Unit,
    vm: QuranClassicViewModel = hiltViewModel()
) {
    BottomBarPlayer(
        modifier = modifier,
        progress = vm.progress,
        item = item,
        isPlaying = isPlaying,
        onAyahChange = onAyahChange,
    )
}

@Composable
fun BottomBarPlayer(
    modifier: Modifier,
    progress: Float,
    item: MediaItem?,
    isPlaying: Boolean,
    onAyahChange: (event: AudioEvents) -> Unit
) {
    Column(modifier = modifier.padding(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SurahInfo(modifier = Modifier, item = item)
            MediaPlayerController(
                modifier = Modifier,
                isPlaying = isPlaying,
                onAyahChange = onAyahChange,
            )
            Slider(modifier = Modifier.weight(1f), value = progress, onValueChange = { onAyahChange(AudioEvents.UpdateProgress(it)) }, valueRange = 0f..100f)
        }
    }
}

@Composable
private fun MediaPlayerController(
    modifier: Modifier,
    isPlaying: Boolean,
    onAyahChange: (event: AudioEvents) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = modifier
            .height(56.dp)
            .padding(4.dp)
    ) {
        PlayerIconItem(icon = Icons.Rounded.SkipPrevious, modifier = Modifier) { onAyahChange(AudioEvents.PlayPrevious) }
        Spacer(modifier = Modifier.size(8.dp))
        PlayerIconItem(
            modifier = Modifier, icon = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow
        ) {
            if (isPlaying) onAyahChange(AudioEvents.PauseAudio) else onAyahChange(AudioEvents.PlayAudio)
        }
        Spacer(modifier = Modifier.size(8.dp))
        PlayerIconItem(icon = Icons.Rounded.SkipNext, modifier = Modifier) { onAyahChange(AudioEvents.PlayNext) }
    }
}

@Composable
private fun SurahInfo(
    modifier: Modifier,
    item: MediaItem?,
) {
    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayerIconItem(
            modifier = Modifier,
            icon = Icons.Rounded.MusicNote,
            borderStroke = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.onSurface)
        ) {

        }
        Spacer(modifier = Modifier.size(8.dp))
        Column {
            Text(
                text = item?.mediaMetadata?.title?.toString() ?: "",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f),
                maxLines = 1
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(text = item?.mediaMetadata?.subtitle?.toString() ?: "", style = MaterialTheme.typography.bodySmall, maxLines = 1)
        }
    }
}

@Composable
private fun PlayerIconItem(
    modifier: Modifier,
    icon: ImageVector,
    borderStroke: BorderStroke? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    color: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        border = borderStroke,
        modifier = modifier
            .clip(CircleShape)
            .clickable { onClick() },
        contentColor = color,
        color = backgroundColor
    ) {
        Box(modifier = Modifier.padding(4.dp), contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = null)
        }
    }
}