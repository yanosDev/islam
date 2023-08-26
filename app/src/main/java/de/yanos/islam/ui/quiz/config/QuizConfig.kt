@file:OptIn(ExperimentalMaterial3Api::class)

package de.yanos.islam.ui.quiz.config

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.NewLabel
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.R
import de.yanos.islam.util.IslamCheckBox
import de.yanos.islam.util.IslamDivider
import de.yanos.islam.util.IslamRadio
import de.yanos.islam.util.PatternedBackgroung
import de.yanos.islam.util.correctColor
import de.yanos.islam.util.errorColor
import de.yanos.islam.util.goldColor


@Composable
fun QuizConfigurationView(
    modifier: Modifier = Modifier,
    vm: QuizConfigViewModel = hiltViewModel(),
    onOpenQuiz: (quizId: Int) -> Unit
) {
    vm.loadData()
    PatternedBackgroung(modifier = modifier) {
        if (vm.recentForms.isNotEmpty() || vm.state.isNotEmpty()) {
            AnimatedVisibility(visible = vm.recentForms.isEmpty(), enter = fadeIn(), exit = fadeOut()) {
                Column(modifier = modifier.padding(start = 32.dp, end = 32.dp, top = 8.dp)) {
                    ConfigScreen(
                        selections = vm.state,
                        difficulty = vm.difficulty,
                        onStartQuiz = {
                            vm.generateQuizForm { id ->
                                onOpenQuiz(id)
                            }
                        },
                        onDifficultyChanged = vm::onDifficultyChange,
                        onSelectionChanged = vm::updateSelection
                    )
                }
            }
            AnimatedVisibility(visible = vm.recentForms.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                Column(modifier = modifier.padding(start = 32.dp, end = 32.dp, top = 8.dp)) {
                    RecentFormsScreen(
                        forms = vm.recentForms,
                        onOpenQuiz = { id -> onOpenQuiz(id) },
                        onDeleteQuiz = vm::deleteForm,
                        onClearFormerForms = { vm.deleteAllForms() },
                        onCreateNewForm = { vm.recentForms.clear() }
                    )
                }
            }
        }

    }
}

@Composable
fun RecentFormsScreen(
    modifier: Modifier = Modifier,
    forms: List<RecentForm>,
    onOpenQuiz: (Int) -> Unit,
    onDeleteQuiz: (Int) -> Unit,
    onClearFormerForms: () -> Unit,
    onCreateNewForm: () -> Unit
) {
    Column {
        Text(
            modifier = modifier.padding(horizontal = 4.dp, vertical = 8.dp),
            text = stringResource(id = R.string.quiz_config_former_header),
            style = MaterialTheme.typography.headlineSmall
        )
        RecentFormsData(modifier = Modifier.weight(1f), forms = forms, openQuiz = onOpenQuiz, deleteQuiz = onDeleteQuiz)
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onClearFormerForms) {
                Row {
                    Icon(imageVector = Icons.Rounded.DeleteForever, contentDescription = "Delete All", tint = errorColor())
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(id = R.string.quiz_config_former_delete_all), style = MaterialTheme.typography.labelMedium, color = errorColor())
                }
            }
            TextButton(onClick = onCreateNewForm) {
                Row {
                    Icon(imageVector = Icons.Rounded.NewLabel, contentDescription = "New Form")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(id = R.string.quiz_config_former_new_form), style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun ConfigScreen(
    modifier: Modifier = Modifier,
    difficulty: Difficulty,
    selections: MutableList<List<TopicSelection>>,
    onStartQuiz: () -> Unit,
    onDifficultyChanged: (Difficulty) -> Unit,
    onSelectionChanged: (id: Int, isSelected: Boolean) -> Unit,
) {
    SelectionHeader(modifier.padding(bottom = 8.dp), text = R.string.quiz_config_header, onQuizConfigured = onStartQuiz)
    DifficultyHeader(modifier = Modifier.padding(vertical = 4.dp), difficulty = difficulty, onDifficultyChanged = onDifficultyChanged)
    SelectionList(selections = selections, onSelectionChanged = onSelectionChanged)
}

@Composable
fun RecentFormsData(modifier: Modifier, forms: List<RecentForm>, openQuiz: (Int) -> Unit, deleteQuiz: (Int) -> Unit) {
    LazyColumn(modifier = modifier) {
        items(items = forms, key = { it.id }) { form ->
            ElevatedCard(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp), onClick = { openQuiz(form.id) }) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = form.topics, style = MaterialTheme.typography.labelMedium)
                    ScoreItem(color = goldColor(), text = form.count, label = R.string.quiz_config_former_count)
                    ScoreItem(color = correctColor(), text = form.corrects, label = R.string.quiz_config_former_corrects)
                    ScoreItem(color = errorColor(), text = form.failures, label = R.string.quiz_config_former_failures)
                    IslamDivider(modifier = Modifier.padding(vertical = 4.dp))
                    TextButton(modifier = Modifier
                        .align(Alignment.End)
                        .padding(2.dp), onClick = { deleteQuiz(form.id) }) {
                        Row {
                            Icon(imageVector = Icons.Rounded.DeleteOutline, contentDescription = "Delete Form", tint = errorColor())
                            Text(text = stringResource(id = R.string.quiz_config_former_delete), style = MaterialTheme.typography.bodySmall, color = errorColor())
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreItem(
    modifier: Modifier = Modifier,
    color: Color,
    text: String,
    @StringRes label: Int
) {
    Row(modifier = modifier.padding(2.dp)) {
        Text(text = stringResource(id = label), style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = color)
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
    text: Int,
    onQuizConfigured: () -> Unit
) {
    Row(modifier = modifier) {
        Text(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .fillMaxWidth(0.7f), text = stringResource(id = text), style = MaterialTheme.typography.headlineSmall
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
