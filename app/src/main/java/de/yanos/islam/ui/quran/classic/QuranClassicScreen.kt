@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package de.yanos.islam.ui.quran.classic

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.R
import de.yanos.islam.data.model.quran.Ayah
import de.yanos.islam.data.model.quran.Page
import de.yanos.islam.ui.quran.audio.AyahAudioPlayer
import de.yanos.islam.util.IslamDivider
import de.yanos.islam.util.NavigationAction
import de.yanos.islam.util.QuranText
import de.yanos.islam.util.alternatingColors
import de.yanos.islam.util.arabicNumber
import de.yanos.islam.util.ayahWithColoredNumber
import de.yanos.islam.util.bodyMedium
import de.yanos.islam.util.labelMedium
import de.yanos.islam.util.quranInnerColor
import de.yanos.islam.util.quranTypoByConfig
import kotlinx.coroutines.launch


@Composable
fun QuranClassicScreen(
    modifier: Modifier = Modifier,
    quranViewModel: QuranClassicViewModel = hiltViewModel(),
    onNavigationChange: (NavigationAction) -> Unit
) {
    val typo = quranTypoByConfig(quranViewModel.quranSizeFactor, quranViewModel.quranStyle)
    QuranContent(
        modifier = modifier,
        ayah = quranViewModel.referenceAyah,
        index = (quranViewModel.referenceAyah?.page ?: 0) - 1,
        pages = quranViewModel.pages,
        onPageSelected = { page: Int -> quranViewModel.onSelectionChange(PageSelection(page)) },
        onSurahSelected = { surahID: Int -> quranViewModel.onSelectionChange(SurahSelection(surahID)) },
        onJuzSelected = { juz: Int -> quranViewModel.onSelectionChange(JuzSelection(juz)) },
        onAyahSelected = { ayah: Ayah -> quranViewModel.onSelectionChange(AyahSelection(ayah.id)) },
        typo = typo
    )
    if (quranViewModel.showDetailSheet)
        AyahDetailBottomSheet(
            modifier = modifier,
            typo = typo
        )
}


@Composable
fun QuranContent(
    modifier: Modifier,
    ayah: Ayah?,
    pages: List<Page>,
    index: Int,
    onSurahSelected: (Int) -> Unit,
    onPageSelected: (Int) -> Unit,
    onJuzSelected: (Int) -> Unit,
    onAyahSelected: (ayah: Ayah) -> Unit,
    typo: Typography
) {
    val pageCount = pages.size
    val pagerState = rememberPagerState { pageCount }
    val scope = rememberCoroutineScope()

    if (index >= 0)
        DisposableEffect(index) {
            scope.launch {
                pagerState.animateScrollToPage(index)
            }
            onDispose { }
        }

    HorizontalPager(modifier = Modifier.fillMaxSize(), state = pagerState, reverseLayout = true) { pageNumber ->
        Column(modifier = modifier.padding(12.dp)) {
            QuranHeader(
                modifier = Modifier.wrapContentHeight(),
                page = pages[pageNumber],
                onSurahSelected = onSurahSelected,
                onJuzSelected = onJuzSelected,
                onPageSelected = onPageSelected,
                typo = typo,
            )
            QuranPage(
                modifier = Modifier,
                page = pages[pageNumber],
                selectedAyah = ayah,
                style = typo.headlineMedium,
                onAyahSelected = onAyahSelected
            )
        }
    }
}

@Composable
private fun QuranHeader(
    modifier: Modifier, page: Page, typo: Typography,
    onSurahSelected: (Int) -> Unit,
    onPageSelected: (Int) -> Unit,
    onJuzSelected: (Int) -> Unit,
) {
    Row(modifier = modifier.wrapContentHeight(), verticalAlignment = Alignment.CenterVertically) {
        TextButton(
            onClick = { onJuzSelected(page.pageSurahId) },
        ) {
            Text(textAlign = TextAlign.Center, text = stringResource(id = R.string.sure_list_cuz, arabicNumber(page.ayahs.first().juz)), style = typo.labelLarge)
        }
        TextButton(
            onClick = { onSurahSelected(page.pageSurahId) },
            modifier = Modifier
                .weight(1f),
        ) {
            Text(textAlign = TextAlign.Center, text = page.pageSurahName, style = typo.headlineSmall)
        }
        TextButton(
            onClick = { onPageSelected(page.pageSurahId) },
        ) {
            Text(textAlign = TextAlign.Center, text = stringResource(id = R.string.sure_list_page, arabicNumber(page.page)), style = typo.labelLarge)
        }
    }
}

@Composable
private fun QuranPage(
    modifier: Modifier,
    page: Page,
    selectedAyah: Ayah?,
    style: TextStyle,
    onAyahSelected: (ayah: Ayah) -> Unit
) {
    Box(modifier = modifier.wrapContentHeight(), contentAlignment = Alignment.Center) {
        OutlinedCard(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            shape = CutCornerShape(2.dp)
        ) {
            OutlinedCard(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                shape = CutCornerShape(2.dp),
            ) {
                OutlinedCard(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = CutCornerShape(2.dp),
                ) {
                    OutlinedCard(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = CutCornerShape(1.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .wrapContentHeight()
                                .fillMaxWidth()
                                .background(color = quranInnerColor.copy(alpha = 0.15f))
                        ) {
                            val scrollState = rememberScrollState()
                            val ayahsOffsets = mutableMapOf<Int, Int>()
                            val ayahs = page.ayahs.mapIndexed { index, ayah ->
                                val ayahText = ayahWithColoredNumber(text = ayah.text, ayahNr = ayah.number, fontSize = style.fontSize, isSelected = ayah == selectedAyah)
                                ayahsOffsets[ayah.id] = if (index == 0) ayahText.length else ayahText.length + (ayahsOffsets[ayah.id - 1] ?: 0)
                                ayahText
                            }
                            val text = ayahs.reduceRight { first, second -> first.plus(second) }
                            QuranText {
                                ClickableText(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp, vertical = 2.dp)
                                        .verticalScroll(scrollState),
                                    text = text,
                                    style = style.copy(textAlign = TextAlign.Justify),
                                    onClick = { offset ->
                                        ayahsOffsets.filter { offset < it.value }.toSortedMap().firstKey().let { id ->
                                            page.ayahs.find { it.id == id }?.let(onAyahSelected)
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AyahDetailBottomSheet(
    modifier: Modifier,
    quranViewModel: QuranClassicViewModel = hiltViewModel(),
    typo: Typography,
) {
    val onEvent = { event: AudioEvents ->
        quranViewModel.onAudioEvents(event)
    }
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = { onEvent(AudioEvents.CloseAudio) }
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
                    text = stringResource(id = R.string.sure_list_cuz, arabicNumber(quranViewModel.referenceAyah?.juz ?: 0)),
                    style = labelMedium(),
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(id = R.string.sure_list_page, arabicNumber(quranViewModel.referenceAyah?.page ?: 0)),
                    style = labelMedium(),
                    textAlign = TextAlign.End
                )
            }
            AyahAudioPlayer(modifier = Modifier, item = quranViewModel.referenceAyah, isPlaying = quranViewModel.isPlaying, onAyahChange = onEvent)
            Spacer(modifier = Modifier.height(8.dp))
            IslamDivider(color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = alternatingColors(
                    text = quranViewModel.referenceAyah?.text ?: "",
                    delimiter = Regex("-|\\s")
                ),
                style = typo.headlineMedium.copy(fontSize = typo.headlineLarge.fontSize.times(1.2)),
                textAlign = TextAlign.End
            )
            Spacer(modifier = Modifier.height(8.dp))
            IslamDivider(color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = alternatingColors(text = quranViewModel.referenceAyah?.transliterationEn ?: "", delimiter = Regex("-|\\s")), style = bodyMedium())
            Spacer(modifier = Modifier.height(8.dp))
            IslamDivider(color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = alternatingColors(text = quranViewModel.referenceAyah?.translationTr ?: "", delimiter = Regex("-|\\s")), style = bodyMedium())
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}