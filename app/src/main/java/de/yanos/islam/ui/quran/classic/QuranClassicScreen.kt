@file:OptIn(ExperimentalFoundationApi::class)

package de.yanos.islam.ui.quran.classic

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.data.model.quran.Page
import de.yanos.islam.util.NavigationAction
import de.yanos.islam.util.QuranText
import de.yanos.islam.util.arabicNumber
import de.yanos.islam.util.headlineSmall
import de.yanos.islam.util.labelLarge
import de.yanos.islam.util.quranInnerColor
import de.yanos.islam.util.quranTypoByConfig


@Composable
fun QuranClassicScreen(
    modifier: Modifier = Modifier,
    vm: QuranClassicViewModel = hiltViewModel(),
    onNavigationChange: (NavigationAction) -> Unit
) {
    Column(modifier = modifier) {
        (vm.state as? QuranState)?.let {
            val currentPage = it.pages[it.currentPage]
            QuranHeader(modifier = Modifier.padding(horizontal = 12.dp), surahName = currentPage.pageSurahName, page = arabicNumber(it.currentPage))
            QuranPages(modifier = Modifier.padding(horizontal = 12.dp), pages = it.pages, style = quranTypoByConfig(vm.quranSizeFactor, vm.quranStyle).headlineMedium)
        }
    }
}

@Composable
fun QuranPages(modifier: Modifier, pages: List<Page>, style: TextStyle) {
    val pageCount = pages.size
    val pagerState = rememberPagerState {
        pageCount
    }

    HorizontalPager(modifier = modifier, state = pagerState, reverseLayout = true) {
        QuranPage(modifier = Modifier, page = pages[it], style = style)
    }
}

@Composable
private fun QuranHeader(modifier: Modifier, surahName: String, page: String) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(modifier = Modifier.weight(1f), textAlign = TextAlign.Center, text = surahName, style = headlineSmall())
        Text(text = arabicNumber(page.toInt()), style = labelLarge())
    }
}

@Composable
private fun QuranPage(modifier: Modifier, page: Page, style: TextStyle) {
    Box(modifier = modifier) {
        OutlinedCard(
            modifier = modifier
                .fillMaxSize(),
            shape = CutCornerShape(2.dp)
        ) {
            OutlinedCard(
                modifier = Modifier
                    .fillMaxSize(),
                shape = CutCornerShape(2.dp),
            ) {
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    shape = CutCornerShape(2.dp),
                ) {
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        shape = CutCornerShape(1.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .background(color = quranInnerColor.copy(alpha = 0.15f))
                                .fillMaxSize()
                        ) {
                            val scrollState = rememberScrollState()
                            QuranText {
                                Text(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp, vertical = 2.dp)
                                        .verticalScroll(scrollState),
                                    textAlign = TextAlign.Justify,
                                    text = page.ayahs.joinToString("") { ayah -> ayah.text + "\uFD3F" + arabicNumber(ayah.number) + "\uFD3E " }.replace("  ", " "),
                                    style = style
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}
