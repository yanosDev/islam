package de.yanos.islam.ui.quran.partial

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.R
import de.yanos.islam.util.IslamDivider
import de.yanos.islam.util.IslamSwitch
import de.yanos.islam.util.NavigationAction
import de.yanos.islam.util.alternatingColors
import de.yanos.islam.util.bodyMedium
import de.yanos.islam.util.headlineSmall
import de.yanos.islam.util.labelLarge
import de.yanos.islam.util.labelSmall
import de.yanos.islam.util.quranTypoByConfig
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
    var showSettings by remember { mutableStateOf(false) }
    if (vm.surah.originals.isNotEmpty()) {

        val onScrollTo = { newPosition: Int ->
            position = newPosition - 1
            scope.launch {
                state.animateScrollToItem(position)
            }
            Unit
        }
        Column(modifier = modifier) {
            Column(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.background)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SureHeader(
                    modifier = Modifier.wrapContentHeight(),
                    sureName = vm.surah.name,
                    hasPreviousSure = vm.previousSurahId != null,
                    hasNextSure = vm.nextSurahId != null,
                    onPreviousSure = {
                        vm.loadSurah(vm.previousSurahId!!)
                        onScrollTo(1)
                    },
                    onNextSure = {
                        vm.loadSurah(vm.nextSurahId!!)
                        onScrollTo(1)
                    })
                AyetSettings(
                    showSettings = showSettings,
                    showTranslations = vm.surah.showTranslation,
                    showPronunciations = vm.surah.showPronunciation,
                    onChangeShowingTranslations = { vm.updateTranslationsVisibility(it) },
                    onChangeShowingPronunciations = { vm.updatePronunciationsVisibility(it) },
                    ayetCount = vm.surah.originals.size,
                    currentPosition = position,
                    onAyetChosen = onScrollTo
                )
                IconButton(onClick = { showSettings = !showSettings }) {
                    Icon(
                        modifier = Modifier
                            .height(24.dp)
                            .width(24.dp),
                        imageVector = if (showSettings) Icons.Rounded.ArrowUpward else Icons.Rounded.ArrowDownward,
                        contentDescription = "OpenSettings"
                    )
                }
            }
            AyetList(modifier = Modifier.weight(1f), state = state, sure = vm.surah, typo = quranTypoByConfig(vm.quranSizeFactor, vm.quranStyle))
        }
    }
}

@Composable
private fun AyetSettings(
    showSettings: Boolean,
    showTranslations: Boolean,
    showPronunciations: Boolean,
    onChangeShowingTranslations: (Boolean) -> Unit,
    onChangeShowingPronunciations: (Boolean) -> Unit,
    ayetCount: Int,
    currentPosition: Int,
    onAyetChosen: (Int) -> Unit
) {
    AnimatedVisibility(visible = showSettings) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            IslamSwitch(isChecked = showTranslations, onCheckChange = onChangeShowingTranslations) {
                Text(text = stringResource(id = R.string.partial_show_translations))
            }
            Spacer(modifier = Modifier.height(2.dp))
            IslamSwitch(isChecked = showPronunciations, onCheckChange = onChangeShowingPronunciations) {
                Text(text = stringResource(id = R.string.partial_show_pronunciations))
            }
            Spacer(modifier = Modifier.height(2.dp))
            AyetChooser(
                size = ayetCount,
                current = currentPosition,
                onAyetChoosen = onAyetChosen
            )
        }
    }
}

@Composable
fun SureHeader(
    modifier: Modifier = Modifier,
    sureName: String,
    hasNextSure: Boolean,
    hasPreviousSure: Boolean,
    onNextSure: () -> Unit,
    onPreviousSure: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousSure, enabled = hasPreviousSure) {
            Icon(
                modifier = Modifier
                    .height(36.dp)
                    .width(36.dp),
                imageVector = Icons.Rounded.ChevronLeft,
                contentDescription = "Previous"
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            modifier = Modifier.padding(12.dp), text = sureName, style = headlineSmall()
        )
        Spacer(modifier = Modifier.width(12.dp))
        IconButton(onClick = onNextSure, enabled = hasNextSure) {
            Icon(
                modifier = Modifier
                    .height(36.dp)
                    .width(36.dp),
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = "Previous"
            )
        }
    }
}

@Composable
private fun AyetList(
    modifier: Modifier = Modifier,
    state: LazyListState,
    sure: SurahData,
    typo: Typography
) {
    LazyColumn(modifier = modifier, state = state) {
        items(count = sure.originals.size) { index: Int ->
            AyetItem(
                index = index,
                original = sure.originals[index],
                translations = sure.translations[index],
                pronunciations = sure.pronunciations[index],
                showTranslations = sure.showTranslation,
                showPronunciations = sure.showPronunciation,
                typo = typo
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AyetChooser(
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
private fun AyetItem(
    modifier: Modifier = Modifier,
    index: Int,
    original: String,
    translations: String,
    pronunciations: String,
    showTranslations: Boolean,
    showPronunciations: Boolean,
    typo: Typography
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
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                text = alternatingColors(text = original),
                style = typo.headlineMedium
            )
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
