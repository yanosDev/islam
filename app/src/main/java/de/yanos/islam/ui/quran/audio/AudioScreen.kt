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
import androidx.compose.material3.BottomAppBar
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.R
import de.yanos.islam.data.model.quran.Ayah

@Composable
fun AudioPlayerScreen(
    modifier: Modifier,
    surahId: Int?,
    ayahId: Int?,
    vm: AudioViewModel = hiltViewModel()
) {
    vm.loadAudioData(surahId, ayahId)
    vm.currentPlaying?.let { ayah ->
        BottomBarPlayer(
            modifier = modifier,
            progress = vm.progress,
            onProgress = { vm.onAudioEvents(AudioEvents.UpdateProgress(it)) },
            ayah = ayah,
            isAudioPlaying = vm.isPlaying,
            onStart = { vm.onAudioEvents(AudioEvents.PlayPause) },
            onNext = { vm.onAudioEvents(AudioEvents.SeekToNext) },
        )
    }

}

@Composable
fun BottomBarPlayer(
    modifier: Modifier,
    progress: Float,
    onProgress: (Float) -> Unit,
    ayah: Ayah,
    isAudioPlaying: Boolean,
    onStart: () -> Unit,
    onNext: () -> Unit,
) {
    BottomAppBar(modifier = modifier) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SurahInfo(modifier = Modifier, ayah = ayah)
                MediaPlayerController(modifier = Modifier, isAudioPlaying = isAudioPlaying, onStart = onStart, onNext = onNext)
                Slider(value = progress, onValueChange = { onProgress(it) }, valueRange = 0f..100f)
            }
        }
    }
}

@Composable
private fun MediaPlayerController(
    modifier: Modifier,
    isAudioPlaying: Boolean,
    onStart: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = modifier
            .height(56.dp)
            .padding(4.dp)
    ) {
        PlayerIconItem(modifier = Modifier, icon = if (isAudioPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow) {
            onStart()
        }
        Spacer(modifier = Modifier.size(8.dp))
        Icon(imageVector = Icons.Rounded.SkipNext, contentDescription = null, modifier = Modifier.clickable { onNext() })
    }
}

@Composable
private fun SurahInfo(
    modifier: Modifier,
    ayah: Ayah,
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
        Spacer(modifier = Modifier.size(4.dp))
        Column {
            Text(
                text = ayah.sureName,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f),
                maxLines = 1
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(text = stringResource(id = R.string.sure_ayet, ayah.number), style = MaterialTheme.typography.bodySmall, maxLines = 1)
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