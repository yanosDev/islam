@file:OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)

package de.yanos.islam.ui.ai

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.R
import de.yanos.islam.util.goldColor

@Composable
fun AIScreen(
    modifier: Modifier,
    vm: AIScreenViewModel = hiltViewModel()
) {
    val replies = vm.conversation.collectAsState(initial = listOf())
    var currentInput by remember { mutableStateOf("") }

    Column(modifier = modifier, verticalArrangement = Arrangement.Bottom) {
        LazyColumn(modifier = Modifier.padding(4.dp), reverseLayout = true) {
            stickyHeader {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(color = goldColor().copy(alpha = 0.3f), shape = RoundedCornerShape(16.dp)),
                    maxLines = 3,
                    value = currentInput,
                    shape = RoundedCornerShape(16.dp),
                    onValueChange = { currentInput = it },
                    trailingIcon = {
                        IconButton(enabled = !vm.requestInProgress, onClick = { vm.sendRequest(currentInput) }) {
                            Icon(imageVector = Icons.Rounded.Send, contentDescription = "", tint = goldColor().copy(alpha = if (vm.requestInProgress) 0.4f else 1f))
                        }
                    })
            }
            if (replies.value.isEmpty())
                item {
                    Column(modifier = modifier.alpha(0.4f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = stringResource(id = R.string.ai_title), style = MaterialTheme.typography.headlineMedium)
                        Text(text = stringResource(id = R.string.ai_description), style = MaterialTheme.typography.titleMedium)
                    }
                }

            if (vm.requestInProgress) {
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(8.dp)
                                .size(36.dp),
                            color = goldColor()
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(100.dp)) }
            replies.value.forEach { request ->
                items(items = request.replies) {
                    BotAnswer(text = it)
                }
                item {
                    Spacer(modifier = Modifier.padding(4.dp))
                }
                item {
                    UserRequest(text = request.question)
                }
                item {
                    Spacer(modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

@Composable
fun UserRequest(
    modifier: Modifier = Modifier,
    text: String
) {
    ElevatedCard(
        modifier = modifier
            .padding(start = 48.dp, end = 4.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = goldColor().copy(alpha = 0.65f)),
        shape = RoundedCornerShape(
            topStart = 16.dp, bottomEnd = 16.dp, bottomStart = 4.dp, topEnd = 4.dp
        )
    ) {
        Text(modifier = Modifier.padding(8.dp), text = text)
    }
}


@Composable
fun BotAnswer(
    modifier: Modifier = Modifier,
    text: String
) {
    ElevatedCard(
        modifier = modifier
            .padding(end = 48.dp, start = 4.dp, bottom = 1.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = goldColor().copy(alpha = 0.05f)),
        shape = RoundedCornerShape(
            topStart = 4.dp, bottomEnd = 4.dp, bottomStart = 16.dp, topEnd = 16.dp
        )
    ) {
        Text(modifier = Modifier.padding(8.dp), text = text)
    }
}
