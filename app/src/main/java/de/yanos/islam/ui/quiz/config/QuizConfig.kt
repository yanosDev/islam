package de.yanos.islam.ui.quiz.config

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.R
import de.yanos.islam.util.IslamCheckBox
import de.yanos.islam.util.IslamDivider
import de.yanos.islam.util.IslamRadio
import de.yanos.islam.util.PatternedBackgroung


@Composable
fun QuizSelectionView(
    modifier: Modifier = Modifier,
    vm: QuizConfigViewModel = hiltViewModel(),
    onQuizConfigured: (quizId: Int) -> Unit
) {
    val onSelectionChanged = { id: Int, isSelected: Boolean ->
        vm.updateSelection(id, isSelected)
    }
    PatternedBackgroung(modifier = modifier) {
        Column(modifier = modifier.padding(start = 32.dp, end = 32.dp, top = 8.dp)) {
            SelectionHeader(modifier.padding(bottom = 8.dp)) {
                vm.generateQuizForm { id ->
                    onQuizConfigured(id)
                }
            }
            DifficultyHeader(modifier = Modifier.padding(vertical = 4.dp), difficulty = vm.difficulty, onDifficultyChanged = vm::onDifficultyChange)
            SelectionList(selections = vm.state, onSelectionChanged = onSelectionChanged)
        }
    }
}

@Composable
private fun SelectionList(
    modifier: Modifier = Modifier,
    selections: MutableList<List<TopicSelection>>,
    onSelectionChanged: (id: Int, isSelected: Boolean) -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 128.dp),
            modifier = modifier
                .widthIn(320.dp, 600.dp)
                .padding(top = 16.dp),
        ) {
            selections.forEach { subList ->
                if (subList.any { it.parentId != null }) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Column {
                            IslamDivider()
                            TopicCheck(
                                topic = subList.first(),
                                onSelectionChanged = onSelectionChanged,
                                isParentEnabledOrNeedless = true
                            )
                        }
                    }
                    items(items = subList.subList(1, subList.size - 1), key = { it.id }) { topic ->
                        TopicCheck(
                            modifier = Modifier.padding(horizontal = 24.dp),
                            topic = topic,
                            onSelectionChanged = onSelectionChanged,
                            isParentEnabledOrNeedless = subList.first().isSelected
                        )
                    }
                } else {
                    items(items = subList, key = { it.id }, span = { GridItemSpan(maxLineSpan) }) { topic ->
                        Column {
                            if (selections.firstOrNull()?.firstOrNull()?.id != topic.id)
                                IslamDivider()
                            TopicCheck(
                                topic = topic,
                                onSelectionChanged = onSelectionChanged,
                                isParentEnabledOrNeedless = true
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DifficultyHeader(
    modifier: Modifier = Modifier,
    difficulty: Difficulty,
    onDifficultyChanged: (Difficulty) -> Unit
) {
    ElevatedCard(modifier = modifier.padding(4.dp)) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(text = stringResource(id = R.string.quiz_config_count, difficulty.count()), style = MaterialTheme.typography.labelMedium)
                Text(text = stringResource(id = R.string.quiz_config_hardness, difficulty.diff()), style = MaterialTheme.typography.labelMedium)
            }
            IslamRadio(modifier = modifier, isSelected = difficulty == Difficulty.Low, text = R.string.quiz_config_low, onClick = { onDifficultyChanged(Difficulty.Low) })
            IslamDivider()
            IslamRadio(modifier = modifier, isSelected = difficulty == Difficulty.Medium, text = R.string.quiz_config_middle, onClick = { onDifficultyChanged(Difficulty.Medium) })
            IslamDivider()
            IslamRadio(modifier = modifier, isSelected = difficulty == Difficulty.High, text = R.string.quiz_config_high, onClick = { onDifficultyChanged(Difficulty.High) })
            IslamDivider()
            IslamRadio(modifier = modifier, isSelected = difficulty == Difficulty.Max, text = R.string.quiz_config_all, onClick = { onDifficultyChanged(Difficulty.Max) })
        }
    }
}

@Composable
fun SelectionHeader(
    modifier: Modifier = Modifier,
    onQuizConfigured: () -> Unit
) {
    Row(modifier = modifier) {
        Text(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .fillMaxWidth(0.7f), text = stringResource(id = R.string.quiz_config_header), style = MaterialTheme.typography.headlineSmall
        )
        TextButton(onClick = onQuizConfigured) {
            Text(text = stringResource(id = R.string.quiz_config_start), style = MaterialTheme.typography.labelMedium)
        }
    }
}


@Composable
private fun TopicCheck(
    modifier: Modifier = Modifier,
    isParentEnabledOrNeedless: Boolean = true,
    topic: TopicSelection,
    onSelectionChanged: (id: Int, isEnabled: Boolean) -> Unit
) {
    IslamCheckBox(
        modifier = modifier,
        isEnabled = isParentEnabledOrNeedless,
        isChecked = topic.isSelected && isParentEnabledOrNeedless,
        onCheckChange = { onSelectionChanged(topic.id, it) }
    ) {
        Text(modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start, text = topic.title, style = MaterialTheme.typography.bodyMedium)
    }
}
