package de.yanos.islam.ui.quran.partial

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.R
import de.yanos.islam.util.IslamDivider
import de.yanos.islam.util.IslamSwitch
import de.yanos.islam.util.NavigationAction
import de.yanos.islam.util.alternatingColors
import de.yanos.islam.util.bodyMedium
import de.yanos.islam.util.headlineLarge
import de.yanos.islam.util.headlineMedium
import de.yanos.islam.util.labelLarge
import de.yanos.islam.util.labelSmall
import de.yanos.islam.util.quranFont
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuranPartialScreen(
    modifier: Modifier = Modifier,
    vm: QuranPartialViewModel = hiltViewModel(),
    onNavigationChange: (NavigationAction) -> Unit = {}
) {
    val state = rememberLazyListState()
    var position by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    if (vm.sure.originals.isNotEmpty()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.padding(12.dp), text = vm.sure.name, style = headlineLarge()
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .widthIn(min = 180.dp, max = 300.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    IslamSwitch(isChecked = vm.sure.showTranslation, onCheckChange = { vm.updateTranslationsVisibility(it) }) {
                        Text(textAlign = TextAlign.Start, text = stringResource(id = R.string.partial_show_translations))
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    IslamSwitch(isChecked = vm.sure.showPronunciation, onCheckChange = { vm.updatePronunciationsVisibility(it) }) {
                        Text(textAlign = TextAlign.Start, text = stringResource(id = R.string.partial_show_pronunciations))
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    AyetChooser(
                        modifier = Modifier.width(120.dp),
                        size = vm.sure.originals.size,
                        current = position,
                        onAyetChoosen = {
                            position = it
                            scope.launch {
                                state.animateScrollToItem(position - 1)
                            }
                        }
                    )
                }
            }
            LazyColumn(modifier = modifier, state = state) {
                items(count = vm.sure.originals.size) { index: Int ->
                    AyetItem(
                        index = index,
                        original = vm.sure.originals[index],
                        translations = vm.sure.translations[index],
                        pronunciations = vm.sure.pronunciations[index],
                        showTranslations = vm.sure.showTranslation,
                        showPronunciations = vm.sure.showPronunciation,
                        quranFont = quranFont(vm.quranFontStyle)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AyetChooser(
    modifier: Modifier = Modifier,
    size: Int,
    current: Int,
    onAyetChoosen: (Int) -> Unit
) {
    if (size > 0) {
        var isExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(modifier = modifier.padding(vertical = 8.dp), expanded = isExpanded, onExpandedChange = { isExpanded = it }) {
            OutlinedTextField(
                value = stringResource(id = R.string.sure_ayet, current + 1),
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
                for (i in 1..size)
                    DropdownMenuItem(
                        modifier = Modifier.wrapContentWidth(),
                        text = {
                            Text(text = stringResource(id = R.string.sure_ayet, i))
                        },
                        onClick = {
                            onAyetChoosen(i)
                            isExpanded = false
                        }
                    )
            }
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
    showPronunciations: Boolean,
    quranFont: FontFamily
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        IslamDivider()
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = stringResource(id = R.string.sure_ayet, index + 1), style = labelLarge())
            Text(modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End, text = alternatingColors(text = original), style = headlineMedium().copy(fontFamily = quranFont))
        }
        AnimatedVisibility(visible = showPronunciations) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = alternatingColors(text = pronunciations, delimiter = Regex("-|\\s")), style = bodyMedium())
        }
        AnimatedVisibility(visible = showTranslations) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = translations, style = bodyMedium())
        }
        Spacer(modifier = Modifier.height(2.dp))
    }
}
