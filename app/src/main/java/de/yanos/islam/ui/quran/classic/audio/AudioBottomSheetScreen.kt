@file:OptIn(ExperimentalMaterial3Api::class)

package de.yanos.islam.ui.quran.classic.audio

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.yanos.islam.R
import de.yanos.islam.data.model.quran.Ayah
import de.yanos.islam.ui.quran.classic.AudioEvents
import de.yanos.islam.util.IslamDivider
import de.yanos.islam.util.alternatingColors
import de.yanos.islam.util.arabicNumber
import de.yanos.islam.util.bodyMedium
import de.yanos.islam.util.labelMedium


@Composable
fun AyahDetailBottomSheet(
    modifier: Modifier,
    ayah: Ayah?,
    isPlaying: Boolean,
    progress: Float,
    onAudioEvents: (AudioEvents) -> Unit,
    typo: Typography,
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = { onAudioEvents(AudioEvents.CloseAudio) }
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                //TODO: Replace with drop down
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(id = R.string.sure_list_cuz, arabicNumber(ayah?.juz ?: 0)),
                    style = labelMedium(),
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(id = R.string.sure_list_page, arabicNumber(ayah?.page ?: 0)),
                    style = labelMedium(),
                    textAlign = TextAlign.End
                )
            }
            AyahAudioPlayer(
                modifier = Modifier,
                item = ayah,
                isPlaying = isPlaying,
                progress = progress,
                onAudioEvents = onAudioEvents
            )
            Spacer(modifier = Modifier.height(8.dp))
            IslamDivider(color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = alternatingColors(
                    text = ayah?.text ?: "",
                    delimiter = Regex("-|\\s")
                ),
                style = typo.headlineMedium.copy(fontSize = typo.headlineLarge.fontSize.times(1.2)),
                textAlign = TextAlign.End
            )
            Spacer(modifier = Modifier.height(8.dp))
            IslamDivider(color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = alternatingColors(text = ayah?.transliterationEn ?: "", delimiter = Regex("-|\\s")), style = bodyMedium())
            Spacer(modifier = Modifier.height(8.dp))
            IslamDivider(color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = alternatingColors(text = ayah?.translationTr ?: "", delimiter = Regex("-|\\s")), style = bodyMedium())
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun AyahAudioPlayer(
    modifier: Modifier,
    progress: Float,
    item: Ayah?,
    isPlaying: Boolean,
    onAudioEvents: (event: AudioEvents) -> Unit
) {
    Column(modifier = modifier.padding(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SurahInfo(modifier = Modifier, item = item)
            MediaPlayerController(
                modifier = Modifier,
                isPlaying = isPlaying,
                onAudioEvents = onAudioEvents,
            )
            Slider(modifier = Modifier.weight(1f), value = progress, onValueChange = { onAudioEvents(AudioEvents.UpdateProgress(it)) }, valueRange = 0f..100f)
        }
    }
}

@Composable
private fun MediaPlayerController(
    modifier: Modifier,
    isPlaying: Boolean,
    onAudioEvents: (event: AudioEvents) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = modifier
            .height(56.dp)
            .padding(4.dp)
    ) {
        PlayerIconItem(icon = Icons.Rounded.SkipPrevious, modifier = Modifier) { onAudioEvents(AudioEvents.PlayPrevious) }
        Spacer(modifier = Modifier.size(8.dp))
        PlayerIconItem(
            modifier = Modifier, icon = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow
        ) {
            if (isPlaying) onAudioEvents(AudioEvents.PauseAudio) else onAudioEvents(AudioEvents.PlayAudio)
        }
        Spacer(modifier = Modifier.size(8.dp))
        PlayerIconItem(icon = Icons.Rounded.SkipNext, modifier = Modifier) { onAudioEvents(AudioEvents.PlayNext) }
    }
}

@Composable
private fun SurahInfo(
    modifier: Modifier,
    item: Ayah?,
) {
    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayerIconItem(
            modifier = Modifier,
            icon = Icons.Rounded.MusicNote,
            borderStroke = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.onSurface)
        ) { }
        Spacer(modifier = Modifier.size(8.dp))
        Column {
            Text(
                text = item?.sureName ?: "",
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(text = stringResource(id = R.string.sure_ayet, item?.number ?: 0), style = MaterialTheme.typography.bodySmall, maxLines = 1)
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