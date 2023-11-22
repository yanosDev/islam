package de.yanos.islam.ui.quran.classic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.util.NavigationAction
import de.yanos.islam.util.QuranText
import de.yanos.islam.util.arabicNumber
import de.yanos.islam.util.quranInnerColor
import de.yanos.islam.util.quranTypoByConfig

@Composable
fun QuranClassicScreen(
    modifier: Modifier = Modifier,
    vm: QuranClassicViewModel = hiltViewModel(),
    onNavigationChange: (NavigationAction) -> Unit
) {

    /**
     * TODO: Border
     * Page Top name of current surah
     * Page Count
     */
    Box(modifier = modifier) {
        OutlinedCard(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
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
                            (vm.state as? QuranState)?.let {
                                QuranText {
                                    Text(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp, vertical = 2.dp)
                                            .verticalScroll(scrollState),
                                        textAlign = TextAlign.Justify,
                                        text = it.pages[0].ayahs.joinToString("") { ayah -> ayah.text + " \uFD3F" + arabicNumber(ayah.number) + "\uFD3E " }.replace("  ", " "),
                                        style = quranTypoByConfig(vm.quranSizeFactor, vm.quranStyle).headlineMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}