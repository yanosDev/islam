package de.yanos.islam.ui.quran.partial

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.R
import de.yanos.islam.util.IslamDivider
import de.yanos.islam.util.NavigationAction
import de.yanos.islam.util.bodyMedium
import de.yanos.islam.util.headlineLarge
import de.yanos.islam.util.headlineMedium
import de.yanos.islam.util.labelMedium
import de.yanos.islam.util.quranFont


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuranPartialScreen(
    modifier: Modifier = Modifier,
    vm: QuranPartialViewModel = hiltViewModel(),
    onNavigationChange: (NavigationAction) -> Unit
) {
    LazyColumn(modifier = Modifier.padding(4.dp)) {
        item {
            Text(modifier = Modifier.padding(12.dp), text = vm.sure.name, style = headlineLarge())
        }
        items(count = vm.sure.originals.size) { index: Int ->
            AyetItem(
                index = index,
                original = vm.sure.originals[index],
                translations = vm.sure.translations[index],
                pronunciations = vm.sure.pronunciations[index],
                showTranslations = vm.sure.showTranslation,
                showPronunciations = vm.sure.showPronunciation,
            )
        }
    }
}

@Composable
fun AyetItem(
    modifier: Modifier = Modifier,
    index: Int,
    original: String,
    translations: String,
    pronunciations: String,
    showTranslations: Boolean,
    showPronunciations: Boolean
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = stringResource(id = R.string.sure_ayet, index + 1), style = labelMedium())
        Spacer(modifier = Modifier.height(2.dp))
        Text(modifier = Modifier.align(Alignment.End), text = original, style = headlineMedium().copy(fontFamily = quranFont))
        AnimatedVisibility(visible = showPronunciations) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = pronunciations, style = bodyMedium())
        }
        AnimatedVisibility(visible = showTranslations) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = translations, style = bodyMedium())
        }
        Spacer(modifier = Modifier.height(2.dp))
        IslamDivider()
    }
}
