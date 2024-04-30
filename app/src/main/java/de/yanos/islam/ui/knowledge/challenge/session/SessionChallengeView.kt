package de.yanos.islam.ui.knowledge.challenge.session

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.core.ui.view.CustomDialog
import de.yanos.islam.R
import de.yanos.islam.data.model.Challenge
import de.yanos.islam.util.IslamCheckBox
import de.yanos.islam.util.IslamDivider
import de.yanos.islam.util.Lottie
import de.yanos.islam.util.NavigationAction
import de.yanos.islam.util.NavigationPath
import de.yanos.islam.util.bodyLarge
import de.yanos.islam.util.bodySmall
import de.yanos.islam.util.correctColor
import de.yanos.islam.util.errorColor
import de.yanos.islam.util.labelMedium
import kotlinx.coroutines.launch

@Composable
fun ChallengeSessionScreen(
    modifier: Modifier = Modifier,
    vm: SessionChallengeViewModel = hiltViewModel(),
    onNavigationChange: (path: NavigationAction) -> Unit
) {
    val state = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val scrollExecutor = { index: Int ->
        scope.launch {
            vm.updateIndex(index)
            state.animateScrollToItem(index = index)
        }
        Unit
    }
    if (vm.currentIndex > 0) {
        DisposableEffect(vm.currentIndex) {
            scope.launch {
                state.animateScrollToItem(index = vm.currentIndex)
            }
            onDispose { }
        }
    }
    ChallengeFinished(vm.challenge.collectAsState(initial = null).value) {
        onNavigationChange(NavigationAction.NavigateBack)
    }
    if (vm.challengeQuizList.size > 0)
        Column(modifier = modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Lottie(modifier = Modifier.height(160.dp), resId = R.raw.lottie_girl_thinking, applyColor = false)
            ChallengeList(modifier = Modifier.weight(1f), state = state, quizList = vm.challengeQuizList)
            ChallengeBoard(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.35f)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                currentIndex = vm.currentIndex,
                quizList = vm.challengeQuizList,
                onShowAnswerChange = { id, showAnswer -> vm.updateAnswerVisibility(id, showAnswer) },
                onAnswerResult = { id, result -> vm.updateQuizResult(id, result) }
            )
            ChallengeNavigation(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                isPreviousButtonEnabled = vm.currentIndex > 0,
                isNextButtonEnabled = vm.currentIndex < vm.challengeQuizList.size - 1,
                currentIndex = vm.currentIndex,
                onScroll = scrollExecutor
            )
        }
}

@Composable
private fun ChallengeFinished(
    challenge: Challenge?,
    onDismiss: () -> Unit
) {
    challenge?.let {
        if (it.finished)
            CustomDialog(
                title = stringResource(id = R.string.challenge_session_finished),
                text = stringResource(id = R.string.challenge_session_finished_result, it.quizList.size, it.solvedQuizList.size, it.failedQuizList.size),
                onConfirm = onDismiss,
                onDismiss = onDismiss,
                showCancel = false
            )
    }
}

@Composable
private fun ChallengeBoard(
    modifier: Modifier = Modifier,
    quizList: List<QuizItem>,
    currentIndex: Int,
    onShowAnswerChange: (id: Int, showAnswer: Boolean) -> Unit,
    onAnswerResult: (id: Int, result: AnswerResult) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        ChallengeScore(modifier = Modifier.fillMaxHeight(), results = quizList.map { it.answerResult })
        ChallengeActionButtons(
            modifier = Modifier.fillMaxHeight(),
            quizList = quizList,
            currentIndex = currentIndex,
            onShowAnswerChange = onShowAnswerChange,
            onAnswerResult = onAnswerResult
        )
    }
}


@Composable
private fun ChallengeList(
    modifier: Modifier = Modifier,
    state: LazyListState,
    quizList: List<QuizItem>,
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        state = state,
        userScrollEnabled = false
    ) {
        items(items = quizList, key = { it.id }) { item ->
            Column(
                modifier = Modifier.fillParentMaxWidth()
            ) {
                Question(modifier = Modifier.weight(1f), item = item, index = quizList.indexOf(item) + 1)
            }
        }
    }
}

@Composable
private fun Question(
    modifier: Modifier = Modifier,
    item: QuizItem,
    index: Int,
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, item.resultColor()),
    ) {
        Text(modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp), text = "$index. ${item.question}?", style = bodySmall())
        IslamDivider(color = item.resultColor())
        AnimatedVisibility(visible = item.showSolution || item.answerResult != AnswerResult.OPEN) {
            Text(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(start = 24.dp, top = 4.dp, bottom = 8.dp, end = 4.dp),
                text = item.answer,
                style = bodyLarge()
            )
        }
    }
}


@Composable
private fun ChallengeActionButtons(
    modifier: Modifier = Modifier,
    quizList: List<QuizItem>,
    currentIndex: Int,
    onShowAnswerChange: (id: Int, showAnswer: Boolean) -> Unit,
    onAnswerResult: (id: Int, result: AnswerResult) -> Unit
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.SpaceEvenly) {
        val solutionVisible = quizList[currentIndex].showSolution || quizList[currentIndex].answerResult != AnswerResult.OPEN
        TextButton(enabled = quizList[currentIndex].answerResult == AnswerResult.OPEN,
            onClick = { onShowAnswerChange(quizList[currentIndex].id, !quizList[currentIndex].showSolution) }
        ) {
            Text(text = stringResource(id = R.string.challenge_session_show_answer), style = labelMedium())
        }
        TextButton(onClick = { onAnswerResult(quizList[currentIndex].id, AnswerResult.CORRECT) }, enabled = solutionVisible) {
            Text(text = stringResource(id = R.string.challenge_session_answer_correct), style = labelMedium(), color = if (solutionVisible) correctColor() else Color.Unspecified)
        }
        TextButton(onClick = { onAnswerResult(quizList[currentIndex].id, AnswerResult.FAILURE) }, enabled = solutionVisible) {
            Text(text = stringResource(id = R.string.challenge_session_answer_failure), style = labelMedium(), color = if (solutionVisible) errorColor() else Color.Unspecified)
        }
    }
}

@Composable
private fun ChallengeScore(
    modifier: Modifier = Modifier,
    results: List<AnswerResult>
) {
    Row(modifier = modifier.fillMaxHeight()) {
        Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = stringResource(id = R.string.challenge_session_total), style = labelMedium())
            Text(text = stringResource(id = R.string.challenge_session_correct), style = labelMedium())
            Text(text = stringResource(id = R.string.challenge_session_error), style = labelMedium())
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = results.size.toString(), style = labelMedium())
            Text(text = results.count { it == AnswerResult.CORRECT }.toString(), style = labelMedium(), color = correctColor())
            Text(text = results.count { it == AnswerResult.FAILURE }.toString(), style = labelMedium(), color = errorColor())
        }
    }
}


@Composable
private fun ChallengeNavigation(
    modifier: Modifier = Modifier,
    isPreviousButtonEnabled: Boolean,
    isNextButtonEnabled: Boolean,
    currentIndex: Int,
    onScroll: (Int) -> Unit
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        TextButton(
            enabled = isPreviousButtonEnabled,
            onClick = { onScroll(currentIndex - 1) }
        ) {
            Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Former Question")
            Text(modifier = Modifier.padding(horizontal = 4.dp), text = stringResource(id = R.string.challenge_session_previous), style = labelMedium())
        }
        TextButton(
            enabled = isNextButtonEnabled,
            onClick = { onScroll(currentIndex + 1) }
        ) {
            Text(modifier = Modifier.padding(horizontal = 4.dp), text = stringResource(id = R.string.challenge_session_next), style = labelMedium())
            Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = "Next Question")
        }
    }
}
