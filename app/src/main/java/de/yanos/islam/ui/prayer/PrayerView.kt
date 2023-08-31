package de.yanos.islam.ui.prayer

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.R
import de.yanos.islam.util.IslamDivider
import de.yanos.islam.util.Lottie
import de.yanos.islam.util.bodySmall
import de.yanos.islam.util.getUserLocation
import de.yanos.islam.util.goldColor
import de.yanos.islam.util.labelMedium
import de.yanos.islam.util.titleSmall

@Composable
fun PrayerScreen(
    modifier: Modifier = Modifier,
    vm: PrayerViewModel = hiltViewModel()
) {
    LazyColumn(modifier = modifier.padding(16.dp)) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Lottie(
                    modifier = Modifier
                        .height(200.dp)
                        .width(200.dp), resId = R.raw.lottie_praying, applyColor = false
                )
                QiblaRug(modifier = Modifier.padding(16.dp), direction = vm.currentState.direction)
            }
        }
        item { PrayingTimes(modifier = Modifier.padding(vertical = 16.dp), times = vm.currentState.times) }
    }
}


@Composable
fun QiblaRug(modifier: Modifier = Modifier, direction: Float) {
    val matrix = Matrix()
    matrix.postRotate(direction)
    val originalBitmap = ImageBitmap.imageResource(id = R.drawable.rug).asAndroidBitmap()
    val rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)
    Box(
        modifier = modifier
            .height(200.dp)
            .width(120.dp)
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.1f), bitmap = originalBitmap.asImageBitmap(), contentDescription = "Testing"
        )
        Image(modifier = Modifier.fillMaxSize(), bitmap = rotatedBitmap.asImageBitmap(), contentDescription = "Testing")
    }

}

@Composable
fun PrayingTimes(
    modifier: Modifier = Modifier,
    times: List<PrayingTime>
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(),
        border = BorderStroke(1.dp, goldColor()),
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
            Text(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally),
                text = stringResource(id = R.string.praying_times_header),
                style = titleSmall()
            )
            times.forEach {
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = stringResource(id = it.textId), style = labelMedium())

                    Text(text = it.remainingTime ?: "Past", style = bodySmall())
                    Text(text = it.timeText, style = bodySmall())
                }
                Spacer(modifier = Modifier.height(2.dp))
                IslamDivider()
            }
        }
    }
}
