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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import de.yanos.islam.data.model.Schedule
import de.yanos.islam.data.model.awqat.AwqatDailyContent
import de.yanos.islam.util.IslamDivider
import de.yanos.islam.util.IslamSwitch
import de.yanos.islam.util.bodySmall
import de.yanos.islam.util.correctColor
import de.yanos.islam.util.goldColor
import de.yanos.islam.util.labelLarge
import de.yanos.islam.util.labelSmall
import de.yanos.islam.util.titleLarge
import de.yanos.islam.util.titleSmall
import kotlin.math.abs

@Composable
fun PrayerScreen(
    modifier: Modifier = Modifier,
    vm: PrayerViewModel = hiltViewModel()
) {
    if (vm.currentState.times.isNotEmpty()) {
        Column(modifier = modifier.verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
            PrayingHeader(
                modifier = Modifier
                    .padding(8.dp)
                    .wrapContentHeight(),
                direction = vm.currentState.times.first().direction
            )
            PrayingTimes(
                modifier = Modifier.wrapContentHeight(),
                times = vm.currentState.times,
                index = vm.currentState.index
            )
            PrayerScheduler(
                modifier = Modifier.wrapContentHeight(),
                schedules = vm.schedules,
                onScheduleChange = vm::changeSchedule
            )
            vm.currentState.dailyContent?.let {
                PrayingDaily(
                    modifier = Modifier.wrapContentHeight(),
                    content = it
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PrayerScheduler(
    modifier: Modifier = Modifier,
    schedules: List<Schedule>,
    onScheduleChange: (id: String, isEnabled: Boolean, relativeTime: Int) -> Unit
) {
    val pageCount = schedules.size
    val pagerState = rememberPagerState(initialPage = 0)

    Column(modifier = modifier) {
        OutlinedCard(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .wrapContentHeight(),
            elevation = CardDefaults.elevatedCardElevation(),
            border = BorderStroke(1.dp, goldColor()),
        ) {
            HorizontalPager(pageCount = pageCount, state = pagerState) {
                ScheduleItem(schedule = schedules[it], onScheduleChange = onScheduleChange)
            }
        }
        DrawDots(pageCount = pageCount, pagerState = pagerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleItem(
    schedule: Schedule,
    onScheduleChange: (id: String, isEnabled: Boolean, relativeTime: Int) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val items = mutableListOf<Int>().apply {
        for (i in 12 downTo 1) {
            add(-i * 5)
        }
        add(0)
        for (i in 1..12) {
            add(i * 5)
        }
    }
    var selectedIndex by remember { mutableStateOf(items.indexOfFirst { it == schedule.relativeTime }) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        IslamSwitch(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            isChecked = schedule.enabled,
            onCheckChange = { onScheduleChange(schedule.id, it, schedule.relativeTime) }) {
            Text(
                text = stringResource(
                    id = when (schedule.ordinal) {
                        0 -> R.string.praying_schedule_fajr
                        1 -> R.string.praying_schedule_sunrise
                        2 -> R.string.praying_schedule_dhuhr
                        3 -> R.string.praying_schedule_asr
                        4 -> R.string.praying_schedule_maghrib
                        else -> R.string.praying_schedule_isha
                    }
                ),
                style = labelLarge(),
                color = goldColor()
            )
        }
        ExposedDropdownMenuBox(modifier = Modifier.padding(vertical = 8.dp), expanded = isExpanded, onExpandedChange = { isExpanded = it }) {
            OutlinedTextField(
                value = stringResource(id = if (schedule.relativeTime < 0) R.string.praying_schedule_before else R.string.praying_schedule_after, abs(schedule.relativeTime)),
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                textStyle = labelSmall()
            )
            ExposedDropdownMenu(
                modifier = Modifier.wrapContentWidth(),
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                items.forEach { value ->
                    DropdownMenuItem(
                        modifier = Modifier.wrapContentWidth(),
                        text = {
                            Text(text = value.toString())
                        },
                        onClick = {
                            onScheduleChange(schedule.id, schedule.enabled, value)
                            isExpanded = false
                        }
                    )
                }
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
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .wrapContentHeight(),
            elevation = CardDefaults.elevatedCardElevation(),
            border = BorderStroke(1.dp, goldColor()),
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = header, style = titleSmall())
                Text(
                    modifier = Modifier
                        .height(120.dp)
                        .verticalScroll(rememberScrollState()), text = text, style = bodySmall()
                )
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
        DrawDots(pageCount = pageCount, pagerState = pagerState)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DrawDots(pageCount: Int, pagerState: PagerState) {

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
