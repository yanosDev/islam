package de.yanos.islam.ui.quiz.session

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.core.ui.view.CustomDialog
import de.yanos.islam.R
import de.yanos.islam.util.IslamCheckBox
import de.yanos.islam.util.IslamDivider
import de.yanos.islam.util.PatternedBackgroung
import de.yanos.islam.util.correctColor
import de.yanos.islam.util.errorColor
import kotlinx.coroutines.launch


@Composable
fun QuizFormView(
    modifier: Modifier = Modifier,
    vm: QuizFormViewModel = hiltViewModel(),
    id: Int,
    goBack: () -> Unit
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
        DisposableEffect(Unit) {
            scope.launch {
                state.animateScrollToItem(index = vm.currentIndex)
            }
            onDispose { }
        }
    }

    vm.populateQuizForm(id)
    PatternedBackgroung(modifier = modifier) {
        vm.form?.let {
            if (it.finished)
                CustomDialog(
                    title = stringResource(id = R.string.quiz_session_finished),
                    text = stringResource(id = R.string.quiz_session_finished_result, it.quizList.size, it.solvedQuizList.size, it.failedQuizList.size),
                    onConfirm = goBack,
                    onDismiss = goBack,
                    showCancel = false
                )
        }
        if (vm.quizList.size > 0)
            Column {
                QuizList(modifier = Modifier.weight(1f), state = state, quizList = vm.quizList)
                QuizBoard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    currentIndex = vm.currentIndex,
                    quizList = vm.quizList,
                    onShowAnswerChange = { id, showAnswer -> vm.updateAnswerVisibility(id, showAnswer) },
                    onAnswerResult = { id, result -> vm.updateQuizResult(id, result) }
                )
                QuizNavigation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    currentIndex = vm.currentIndex,
                    isNextButtonEnabled = vm.currentIndex < vm.quizList.size - 1,
                    isPreviousButtonEnabled = vm.currentIndex > 0,
                    onScroll = scrollExecutor
                )
            }
    }
}

@Composable
private fun QuizBoard(
    modifier: Modifier = Modifier,
    quizList: List<QuizItem>,
    currentIndex: Int,
    onShowAnswerChange: (id: Int, showAnswer: Boolean) -> Unit,
    onAnswerResult: (id: Int, result: AnswerResult) -> Unit
) {
    Row(
        modifier = modifier.fillMaxHeight(0.2f),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        QuizScore(modifier = Modifier.fillMaxHeight(), results = quizList.map { it.answerResult })
        QuizResultButtons(
            modifier = Modifier.fillMaxHeight(),
            quizList = quizList,
            currentIndex = currentIndex,
            onShowAnswerChange = onShowAnswerChange,
            onAnswerResult = onAnswerResult
        )
    }
}

@Composable
private fun QuizResultButtons(
    modifier: Modifier = Modifier,
    quizList: List<QuizItem>,
    currentIndex: Int,
    onShowAnswerChange: (id: Int, showAnswer: Boolean) -> Unit,
    onAnswerResult: (id: Int, result: AnswerResult) -> Unit
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.SpaceEvenly) {
        val solutionVisible = quizList[currentIndex].showSolution || quizList[currentIndex].answerResult != AnswerResult.OPEN
        AnswerDisplayer(
            currentItem = quizList[currentIndex],
            onShowAnswerChange = onShowAnswerChange
        )
        TextButton(onClick = { onAnswerResult(quizList[currentIndex].id, AnswerResult.CORRECT) }, enabled = solutionVisible) {
            Text(
                text = stringResource(id = R.string.quiz_session_answer_correct),
                style = MaterialTheme.typography.labelMedium,
                color = if (solutionVisible) correctColor() else Color.Unspecified
            )
        }
        TextButton(onClick = { onAnswerResult(quizList[currentIndex].id, AnswerResult.FAILURE) }, enabled = solutionVisible) {
            Text(
                text = stringResource(id = R.string.quiz_session_answer_failure),
                style = MaterialTheme.typography.labelMedium,
                color = if (solutionVisible) errorColor() else Color.Unspecified
            )
        }
    }
}

@Composable
private fun QuizScore(modifier: Modifier = Modifier, results: List<AnswerResult>) {
    Row(modifier = modifier.fillMaxHeight()) {
        Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceEvenly) {
            Text(
                text = stringResource(id = R.string.quiz_session_total),
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = stringResource(id = R.string.quiz_session_correct),
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = stringResource(id = R.string.quiz_session_error),
                style = MaterialTheme.typography.labelMedium
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceEvenly) {
            Text(
                text = results.size.toString(),
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = results.count { it == AnswerResult.CORRECT }.toString(),
                style = MaterialTheme.typography.labelMedium,
                color = correctColor()
            )
            Text(
                text = results.count { it == AnswerResult.FAILURE }.toString(),
                style = MaterialTheme.typography.labelMedium,
                color = errorColor()
            )
        }
    }
}

@Composable
private fun AnswerDisplayer(
    modifier: Modifier = Modifier,
    currentItem: QuizItem,
    onShowAnswerChange: (id: Int, showAnswer: Boolean) -> Unit,
) {
    IslamCheckBox(
        modifier = modifier,
        isEnabled = currentItem.answerResult == AnswerResult.OPEN,
        isChecked = currentItem.showSolution || currentItem.answerResult != AnswerResult.OPEN,
        onCheckChange = { onShowAnswerChange(currentItem.id, !currentItem.showSolution) }
    ) {
        Text(
            textAlign = TextAlign.End,
            text = stringResource(id = R.string.quiz_session_show_answer),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun QuizList(
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
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        border = BorderStroke(1.dp, item.resultColor()),
    ) {
        Text(modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp), text = "$index. ${item.question}?", style = MaterialTheme.typography.headlineSmall)
        IslamDivider(color = item.resultColor())
        AnimatedVisibility(visible = item.showSolution || item.answerResult != AnswerResult.OPEN) {
            Text(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(start = 24.dp, top = 4.dp, bottom = 8.dp, end = 4.dp),
                text = item.answer,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun QuizNavigation(
    modifier: Modifier = Modifier,
    isPreviousButtonEnabled: Boolean,
    isNextButtonEnabled: Boolean,
    currentIndex: Int,
    onScroll: (Int) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(
            enabled = isPreviousButtonEnabled,
            onClick = { onScroll(currentIndex - 1) }
        ) {
            Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "Former Question")
            Text(modifier = Modifier.padding(horizontal = 4.dp), text = stringResource(id = R.string.quiz_session_previous), style = MaterialTheme.typography.labelMedium)
        }
        TextButton(
            enabled = isNextButtonEnabled,
            onClick = { onScroll(currentIndex + 1) }
        ) {
            Text(modifier = Modifier.padding(horizontal = 4.dp), text = stringResource(id = R.string.quiz_session_next), style = MaterialTheme.typography.labelMedium)
            Icon(imageVector = Icons.Rounded.ArrowForward, contentDescription = "Next Question")
        }
    }
}