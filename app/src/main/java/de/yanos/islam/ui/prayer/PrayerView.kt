package de.yanos.islam.ui.prayer

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.R
import de.yanos.islam.data.model.awqat.AwqatDailyContent
import de.yanos.islam.util.IslamDivider
import de.yanos.islam.util.bodySmall
import de.yanos.islam.util.correctColor
import de.yanos.islam.util.goldColor
import de.yanos.islam.util.labelSmall
import de.yanos.islam.util.titleLarge
import de.yanos.islam.util.titleSmall

@Composable
fun PrayerScreen(
    modifier: Modifier = Modifier,
    vm: PrayerViewModel = hiltViewModel()
) {
    if (vm.currentState.times.isNotEmpty()) {
        Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            PrayingHeader(
                modifier = Modifier
                    .padding(8.dp)
                    .wrapContentHeight(),
                direction = vm.currentState.times.first().direction
            )
            PrayingTimes(
                modifier = Modifier.wrapContentHeight(),
                times = vm.currentState.times, index = vm.currentState.index
            )
            vm.currentState.dailyContent?.let {
                PrayingDaily(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .wrapContentHeight(),
                    content = it
                )
            }
        }
    }
}

@Composable
private fun PrayingHeader(modifier: Modifier = Modifier, direction: Float) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.praying_times_header),
            style = titleLarge()
        )
        QiblaRug(direction = direction)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PrayingDaily(
    modifier: Modifier,
    content: AwqatDailyContent
) {
    val dailyCard = @Composable { header: String, text: String, textSource: String? ->
        OutlinedCard(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 240.dp),
            elevation = CardDefaults.elevatedCardElevation(),
            border = BorderStroke(1.dp, goldColor()),
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = header, style = titleSmall())
                Text(text = text, style = bodySmall())
                Text(modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End, text = textSource ?: "", style = titleSmall())
            }
        }
    }
    val pageCount = 3
    val pagerState = rememberPagerState(initialPage = 0)
    Column(modifier = Modifier.wrapContentHeight()) {
        HorizontalPager(pageCount = pageCount, state = pagerState) {
            when (it) {
                0 -> dailyCard(stringResource(id = R.string.praying_daily_hadith), content.hadith, content.hadithSource)
                1 -> dailyCard(stringResource(id = R.string.praying_daily_verse), content.verse, content.verseSource)
                else -> dailyCard(stringResource(id = R.string.praying_daily_prayer), content.pray, content.praySource)
            }
        }
        Row(
            Modifier
                .height(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) goldColor() else Color.LightGray.copy(alpha = 0.25F)
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(10.dp)

                )
            }
        }
    }
}

@Composable
private fun QiblaRug(modifier: Modifier = Modifier, direction: Float) {
    val matrix = Matrix()
    matrix.postRotate(direction)
    val originalBitmap = ImageBitmap.imageResource(id = R.drawable.rug).asAndroidBitmap()
    val rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)
    Box(
        modifier = modifier
            .height(60.dp)
            .width(60.dp)
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.1f), bitmap = originalBitmap.asImageBitmap(), contentDescription = "Testing"
        )
        Image(modifier = Modifier.fillMaxSize(), bitmap = rotatedBitmap.asImageBitmap(), contentDescription = "Testing")
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PrayingTimes(
    modifier: Modifier = Modifier,
    times: List<DayData>,
    index: Int
) {
    val pageCount = times.size
    val pagerState = rememberPagerState(initialPage = index)
    HorizontalPager(modifier = modifier, pageCount = pageCount, state = pagerState) {
        val currentDay = times[it]
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = currentDay.day, style = titleSmall(), color = goldColor())
            OutlinedCard(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                elevation = CardDefaults.elevatedCardElevation(),
                border = BorderStroke(1.dp, goldColor()),
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                    currentDay.times.forEach { time ->
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(textAlign = TextAlign.Start, text = stringResource(id = time.textId), style = labelSmall())
                            time.remainingTime?.let {
                                Text(
                                    modifier = Modifier
                                        .padding(horizontal = 18.dp)
                                        .weight(1f),
                                    textAlign = TextAlign.End,
                                    text = it,
                                    style = bodySmall(),
                                    color = goldColor()
                                )
                            }
                            Text(
                                textAlign = TextAlign.End,
                                text = time.timeText,
                                style = bodySmall(),
                                color = if (time.isCurrentTime) correctColor() else Color.Unspecified
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        IslamDivider()
                    }
                }
            }
        }
    }
}