package de.yanos.islam.ui.knowledge.challenge.create

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.core.ui.view.CustomDialog
import de.yanos.islam.R
import de.yanos.islam.util.ChallengeDifficulty
import de.yanos.islam.util.IslamCheckBox
import de.yanos.islam.util.IslamDivider
import de.yanos.islam.util.IslamRadio
import de.yanos.islam.util.KnowledgeNavigationAction
import de.yanos.islam.util.Lottie
import de.yanos.islam.util.NavigationAction
import de.yanos.islam.util.NavigationPath
import de.yanos.islam.util.bodyLarge
import de.yanos.islam.util.bodyMedium
import de.yanos.islam.util.labelMedium
import de.yanos.islam.util.titleLarge

@Composable
fun ChallengeScreen(
    modifier: Modifier = Modifier,
    vm: CreateChallengeViewModel = hiltViewModel(),
    onNavigationChange: (path: NavigationAction) -> Unit
) {
    CreationDialogError(showError = vm.showCreationError) {
        vm.showCreationError = false
    }

    Column(modifier = modifier.padding(16.dp)) {
        Lottie(modifier = Modifier.height(128.dp), resId = R.raw.lottie_configuring, applyColor = false)
        Text(text = stringResource(id = R.string.challenge_creation_title), style = titleLarge())
        Spacer(modifier = Modifier.height(4.dp))
        ChallengeDifficulty(modifier = Modifier.padding(vertical = 8.dp), difficulty = vm.difficulty) { difficulty: ChallengeDifficulty -> vm.difficulty = difficulty }
        ChallengeTopics(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp), topics = vm.topics, onSelectionChanged = vm::updateSelection
        )
        Row(
            modifier = Modifier
                .align(Alignment.End)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            AnimatedVisibility(visible = vm.hasOpenChallenges.collectAsState(initial = false).value) {
                TextButton(onClick = {
                    onNavigationChange(KnowledgeNavigationAction.NavigateToOpenChallenges)
                }) {
                    Text(text = stringResource(id = R.string.challenge_creation_to_open), style = bodyLarge())
                }
            }
            TextButton(onClick = {
                vm.createForm { id ->
                    onNavigationChange(KnowledgeNavigationAction.NavigateToChallenge(id))
                }
            }) {
                Text(text = stringResource(id = R.string.challenge_creation_create), style = bodyLarge())
            }
        }
    }
}

@Composable
private fun ChallengeDifficulty(
    modifier: Modifier = Modifier,
    difficulty: ChallengeDifficulty,
    onDifficultyChanged: (ChallengeDifficulty) -> Unit
) {
    val composeRadio = @Composable { contentDifficulty: ChallengeDifficulty, stringId: Int ->
        IslamRadio(modifier = Modifier.widthIn(max = 200.dp), isSelected = contentDifficulty == difficulty, text = stringId, onClick = { onDifficultyChanged(contentDifficulty) })
    }
    ElevatedCard(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(text = stringResource(id = R.string.challenge_creation_total_count, difficulty.count()), style = labelMedium())
                Text(text = stringResource(id = R.string.challenge_creation_level, difficulty.diff()), style = labelMedium())
            }
            IslamDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                composeRadio(ChallengeDifficulty.Low, R.string.challenge_creation_difficulty_low)
                composeRadio(ChallengeDifficulty.Medium, R.string.challenge_creation_difficulty_mid)
            }
            IslamDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                composeRadio(ChallengeDifficulty.High, R.string.challenge_creation_difficulty_high)
                composeRadio(ChallengeDifficulty.Max, R.string.challenge_creation_difficulty_max)
            }
        }
    }
}

@Composable
private fun CreationDialogError(
    modifier: Modifier = Modifier,
    showError: Boolean,
    onDismiss: () -> Unit
) {
    if (showError) {
        CustomDialog(
            modifier = modifier,
            title = stringResource(id = R.string.challenge_creation_error_title),
            text = stringResource(id = R.string.challenge_creation_error_content),
            onConfirm = onDismiss,
            onDismiss = onDismiss,
            showCancel = false
        )
    }
}

@Composable
private fun ChallengeTopics(
    modifier: Modifier = Modifier,
    topics: List<List<TopicSelection>>,
    onSelectionChanged: (id: Int, isSelected: Boolean) -> Unit
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 128.dp),
            modifier = Modifier.widthIn(320.dp, 600.dp),
        ) {
            topics.forEach { subList ->
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
                            if (topics.firstOrNull()?.firstOrNull()?.id != topic.id)
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
        Text(modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start, text = topic.title, style = bodyMedium())
    }
}