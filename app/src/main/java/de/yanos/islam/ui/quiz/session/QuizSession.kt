package de.yanos.islam.ui.quiz.session

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.R
import de.yanos.islam.util.IslamCheckBox
import de.yanos.islam.util.IslamDivider
import de.yanos.islam.util.PatternedBackgroung
import kotlinx.coroutines.launch


@Composable
fun QuizFormView(
    modifier: Modifier = Modifier,
    vm: QuizFormViewModel = hiltViewModel(),
    id: Int
) {
    val state = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val scrollExecutor = { index: Int ->
        scope.launch {
            vm.currentIndex = index
            state.animateScrollToItem(index = index)
        }
        Unit
    }

    vm.populateQuizForm(id)
    PatternedBackgroung(modifier = modifier) {
        if (vm.quizList.size > 0)
            Column {
                QuizList(modifier = Modifier.weight(1f), state = state, quizList = vm.quizList)
                AnswerDisplayer(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 8.dp)
                        .wrapContentWidth(),
                    currentItem = vm.quizList[vm.currentIndex],
                    onShowAnswerChange = { id, showAnswer -> vm.updateAnswerVisibility(id, showAnswer) }
                )
                ScrollButtons(
                    currentIndex = vm.currentIndex,
                    isNextButtonEnabled = vm.currentIndex < vm.quizList.size - 1,
                    isPreviousButtonEnabled = vm.currentIndex > 0,
                    onScroll = scrollExecutor
                )
            }
    }
}

@Composable
fun AnswerDisplayer(
    modifier: Modifier,
    currentItem: QuizItem,
    onShowAnswerChange: (Int, Boolean) -> Unit
) {
    IslamCheckBox(
        modifier = modifier,
        isChecked = currentItem.showSolution,
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
fun QuizList(
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
                Question(modifier = Modifier.weight(1f), item = item)
            }
        }
    }
}

@Composable
fun Question(
    modifier: Modifier = Modifier,
    item: QuizItem
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp), text = "${item.question}?", style = MaterialTheme.typography.headlineSmall)
        AnimatedVisibility(visible = item.showSolution) {
            IslamDivider()
            Text(modifier = Modifier.padding(start = 24.dp, top = 4.dp, bottom = 8.dp, end = 4.dp), text = item.answer, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun ScrollButtons(
    modifier: Modifier = Modifier,
    isPreviousButtonEnabled: Boolean,
    isNextButtonEnabled: Boolean,
    currentIndex: Int,
    onScroll: (Int) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(
            enabled = isPreviousButtonEnabled,
            onClick = { onScroll(currentIndex - 1) }
        ) {
            Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "Former Question")
            Text(modifier = Modifier.padding(4.dp), text = stringResource(id = R.string.quiz_session_previous), style = MaterialTheme.typography.labelMedium)
        }
        TextButton(
            enabled = isNextButtonEnabled,
            onClick = { onScroll(currentIndex + 1) }
        ) {
            Text(modifier = Modifier.padding(4.dp), text = stringResource(id = R.string.quiz_session_next), style = MaterialTheme.typography.labelMedium)
            Icon(imageVector = Icons.Rounded.ArrowForward, contentDescription = "Next Question")
        }
    }
}
