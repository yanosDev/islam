package de.yanos.islam.ui.knowledge.challenge.open

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.NewLabel
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.R
import de.yanos.islam.util.IslamDivider
import de.yanos.islam.util.KnowledgeNavigationAction
import de.yanos.islam.util.NavigationAction
import de.yanos.islam.util.bodySmall
import de.yanos.islam.util.correctColor
import de.yanos.islam.util.errorColor
import de.yanos.islam.util.goldColor
import de.yanos.islam.util.labelMedium
import de.yanos.islam.util.titleLarge

@Composable
fun OpenChallengesScreen(
    modifier: Modifier = Modifier,
    vm: OpenChallengesViewModel = hiltViewModel(),
    onNavigationChange: (path: NavigationAction) -> Unit
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = stringResource(id = R.string.challenge_creation_header),
            style = titleLarge()
        )
        Spacer(modifier = Modifier.height(4.dp))
        ChallengeList(
            modifier = Modifier.weight(1f),
            challenges = vm.challenges.collectAsState(initial = listOf()).value,
            onChallengeClicked = { onNavigationChange(KnowledgeNavigationAction.NavigateToChallenge(it)) },
            onDeleteChallengeClicked = vm::deleteOpenChallenge
        )
        Spacer(modifier = Modifier.height(4.dp))
        ChallengeActionButtons(
            onDeleteAllChallengesClicked = vm::deleteAllOpenChallenges,
            onNavigateToNewChallenge = { onNavigationChange(KnowledgeNavigationAction.NavigateToChallengeCreation) }
        )
    }
}

@Composable
private fun ChallengeActionButtons(
    modifier: Modifier = Modifier,
    onDeleteAllChallengesClicked: () -> Unit,
    onNavigateToNewChallenge: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(onClick = onDeleteAllChallengesClicked) {
            Row {
                Icon(imageVector = Icons.Rounded.DeleteForever, contentDescription = "Delete All", tint = errorColor())
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(id = R.string.challenge_creation_delete_all),
                    style = labelMedium(),
                    color = errorColor()
                )
            }
        }
        TextButton(onClick = onNavigateToNewChallenge) {
            Row {
                Icon(imageVector = Icons.Rounded.NewLabel, contentDescription = "New Form")
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = stringResource(id = R.string.challenge_creation_new_form), style = labelMedium())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun ChallengeList(
    modifier: Modifier = Modifier,
    challenges: List<OpenChallenge>,
    onChallengeClicked: (id: Int) -> Unit,
    onDeleteChallengeClicked: (id: Int) -> Unit
) {
    val scoreItem = @Composable { color: Color, text: String, label: Int ->
        Row(modifier = Modifier.padding(2.dp)) {
            Text(text = stringResource(id = label), style = bodySmall())
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = text, style = bodySmall(), color = color)
        }
    }
    LazyColumn(modifier = modifier) {
        items(items = challenges, key = { it.id }) { form ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement()
                    .padding(vertical = 4.dp),
                onClick = { onChallengeClicked(form.id) }) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = form.topics ?: "", style = labelMedium())
                    scoreItem(goldColor(), form.count.takeIf { it.toInt() != Int.MAX_VALUE } ?: stringResource(id = R.string.challenge_creation_difficulty_max), R.string.challenge_creation_count)
                    scoreItem(correctColor(), form.corrects.size.toString(), R.string.challenge_creation_corrects)
                    scoreItem(errorColor(), form.failures.size.toString(), R.string.challenge_creation_failures)
                    Spacer(modifier = Modifier.height(4.dp))
                    IslamDivider()
                    TextButton(modifier = Modifier
                        .align(Alignment.End)
                        .padding(2.dp), onClick = { onDeleteChallengeClicked(form.id) }) {
                        Row {
                            Icon(imageVector = Icons.Rounded.DeleteOutline, contentDescription = "Delete Form", tint = errorColor())
                            Text(text = stringResource(id = R.string.challenge_creation_delete), style = bodySmall(), color = errorColor())
                        }
                    }
                }
            }
        }
    }
}
