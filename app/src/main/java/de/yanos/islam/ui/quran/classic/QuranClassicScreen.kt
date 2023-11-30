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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.rounded.NavigateNext
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.R
import de.yanos.islam.data.model.QuranBookmark
import de.yanos.islam.data.model.quran.Ayah
import de.yanos.islam.data.model.quran.Page
import de.yanos.islam.ui.quran.classic.audio.AyahDetailBottomSheet
import de.yanos.islam.util.Constants
import de.yanos.islam.util.QuranText
import de.yanos.islam.util.arabicNumber
import de.yanos.islam.util.ayahWithColoredNumber
import de.yanos.islam.util.quranInnerColor
import de.yanos.islam.util.quranTypoByConfig
import kotlinx.coroutines.launch


@Composable
fun QuranClassicScreen(
    modifier: Modifier = Modifier,
    quranViewModel: QuranClassicViewModel = hiltViewModel()
) {
    val typo = quranTypoByConfig(quranViewModel.quranSizeFactor, quranViewModel.quranStyle)
    val onBookmarkSelected = { bookmark: QuranBookmark -> quranViewModel.onSelectionChange(BookmarkSelection(bookmark), false) }

    QuranContent(
        modifier = modifier,
        ayah = quranViewModel.referenceAyah,
        index = (quranViewModel.referenceAyah?.page ?: 0) - 1,
        pages = quranViewModel.pages,
        onAyahSelected = { ayah: Ayah -> quranViewModel.onSelectionChange(AyahSelection(ayah.id)) },
        onShowQuickMenu = { quranViewModel.showQuickMenu = true },
        onAddBookmark = quranViewModel::createBookmark,
        onPageChange = { quranViewModel.updateCurrentPage(it) },
        initPage = quranViewModel.initPage,
        typo = typo
    )
    if (quranViewModel.showAyahDetails)
        AyahDetailBottomSheet(
            modifier = modifier,
            typo = typo,
            progress = quranViewModel.progress,
            ayah = quranViewModel.referenceAyah,
            isPlaying = quranViewModel.isPlaying,
            onAudioEvents = { event ->
                quranViewModel.onAudioEvents(event)
            }
        )
    if (quranViewModel.showQuickMenu)
        QuickMenu(
            modifier = modifier,
            currentPage = quranViewModel.referenceAyah?.page ?: 1,
            maxPage = Constants.QURAN_PAGES,
            currentJuz = quranViewModel.referenceAyah?.juz ?: 1,
            maxJuz = Constants.JUZ,
            marks = quranViewModel.quickMarks.collectAsState(initial = listOf()).value,
            onPageSelected = { page: Int -> quranViewModel.onSelectionChange(PageSelection(page), false) },
            onJuzSelected = { juz: Int -> quranViewModel.onSelectionChange(JuzSelection(juz), false) },
            onBookmark = onBookmarkSelected
        ) {
            quranViewModel.showQuickMenu = false
        }
}


@Composable
private fun QuranContent(
    modifier: Modifier,
    ayah: Ayah?,
    pages: List<Page>,
    index: Int,
    onAyahSelected: (ayah: Ayah) -> Unit,
    onAddBookmark: (page: Page) -> Unit,
    onPageChange: (pageIndex: Int) -> Unit,
    onShowQuickMenu: () -> Unit,
    typo: Typography,
    initPage: Int
) {
    val pageCount = pages.size
    val pagerState = rememberPagerState(initialPage = initPage) { pageCount }
    val scope = rememberCoroutineScope()

    if (index >= 0 && pages.isNotEmpty())
        DisposableEffect(index) {
            scope.launch {
                pagerState.animateScrollToPage(index)
            }
            onDispose { }
        }

    HorizontalPager(modifier = Modifier.fillMaxSize(), state = pagerState, reverseLayout = true) { pageNumber ->
        onPageChange(pagerState.currentPage)
        Column(modifier = modifier.padding(12.dp)) {
            QuranHeader(
                modifier = Modifier.wrapContentHeight(),
                page = pages[pageNumber],
                onShowQuickMenu = onShowQuickMenu,
                onAddBookmark = onAddBookmark,
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
    modifier: Modifier,
    page: Page,
    typo: Typography,
    onShowQuickMenu: () -> Unit,
    onAddBookmark: (page: Page) -> Unit
) {
    Row(
        modifier = modifier
            .wrapContentHeight()
            .heightIn(72.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(textAlign = TextAlign.Center, text = stringResource(id = R.string.sure_list_cuz, arabicNumber(page.ayahs.first().juz)), style = typo.labelLarge)
        IconButton(onClick = { onAddBookmark(page) }) {
            Icon(imageVector = Icons.Filled.BookmarkAdd, contentDescription = null)
        }
        Text(modifier = Modifier.weight(1f), textAlign = TextAlign.Center, text = page.pageSurahName, style = typo.headlineSmall)
        IconButton(onClick = onShowQuickMenu) {
            Icon(imageVector = Icons.Filled.Bookmarks, contentDescription = null)
        }
        Text(textAlign = TextAlign.Center, text = stringResource(id = R.string.sure_list_page, arabicNumber(page.page)), style = typo.labelLarge)
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
    val scrollState = rememberScrollState()
    var scrollToBottom by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    DisposableEffect(scrollToBottom) {
        if (scrollToBottom)
            scope.launch {
                scrollState.animateScrollTo(Int.MAX_VALUE)
            }
        onDispose { }
    }

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
                            val ayahsOffsets = mutableMapOf<Int, Int>()
                            val ayahs = page.ayahs.mapIndexed { index, ayah ->
                                val isSelected = ayah == selectedAyah
                                if (isSelected)
                                    scrollToBottom = index > page.ayahs.size / 2
                                val ayahText = ayahWithColoredNumber(text = ayah.text, ayahNr = ayah.number, fontSize = style.fontSize, isSelected = isSelected)
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
fun QuickMenu(
    modifier: Modifier,
    currentPage: Int,
    maxPage: Int,
    currentJuz: Int,
    maxJuz: Int,
    marks: List<QuranBookmark>,
    onPageSelected: (Int) -> Unit,
    onJuzSelected: (Int) -> Unit,
    onBookmark: (QuranBookmark) -> Unit,
    onDismiss: () -> Unit
) {
    var rememberPage by remember { mutableIntStateOf(currentPage - 1) }
    var rememberJuz by remember { mutableIntStateOf(currentJuz - 1) }
    var rememberBookmark: QuranBookmark? by remember { mutableStateOf(null) }
    var lastChanged by remember { mutableIntStateOf(4) }
    val disMissHandler = {
        when {
            lastChanged < 2 && rememberPage != currentPage - 1 -> onPageSelected(rememberPage + 1)
            lastChanged < 3 && rememberJuz != currentJuz - 1 -> onJuzSelected(rememberJuz + 1)
            lastChanged < 4 -> rememberBookmark?.let(onBookmark)
        }
        onDismiss()
    }
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = disMissHandler
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .wrapContentHeight(), verticalArrangement = Arrangement.Top
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                text = stringResource(id = R.string.sure_list_cuz, rememberJuz + 1),
                style = MaterialTheme.typography.labelLarge
            )
            Slider(value = rememberJuz.toFloat() / maxJuz, onValueChange = {
                rememberJuz = (it * maxJuz).toInt()
                lastChanged = 2
            }, steps = maxJuz - 1)
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                text = stringResource(id = R.string.sure_list_page, rememberPage + 1),
                style = MaterialTheme.typography.labelLarge
            )
            Slider(value = rememberPage.toFloat() / maxPage, onValueChange = {
                rememberPage = (it * maxPage).toInt()
                lastChanged = 1
            }, steps = maxPage - 1)
            Spacer(modifier = Modifier.size(16.dp))
            Text(modifier = Modifier.padding(horizontal = 8.dp), text = stringResource(id = R.string.quran_jump_title), style = MaterialTheme.typography.titleMedium)
            marks.forEach {
                Row(modifier = Modifier.padding(4.dp)) {
                    TextButton(onClick = {
                        rememberBookmark = it
                        lastChanged = 3
                        disMissHandler()
                    }) {
                        Icon(imageVector = Icons.Rounded.NavigateNext, contentDescription = null)
                        Spacer(modifier = Modifier.padding(8.dp))
                        Text(
                            modifier = Modifier.padding(4.dp),
                            text = stringResource(id = R.string.quran_jump_item, it.page, it.juz, it.surahName, it.ayah),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}