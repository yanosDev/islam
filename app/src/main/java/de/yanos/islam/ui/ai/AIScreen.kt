@file:OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)

package de.yanos.islam.ui.ai

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.util.goldColor

@Composable
fun AIScreen(
    modifier: Modifier,
    vm: AIScreenViewModel = hiltViewModel()
) {
    var currentInput by remember { mutableStateOf("") }
    Column(modifier = modifier, verticalArrangement = Arrangement.Bottom) {
        LazyColumn(modifier = Modifier.padding(4.dp), reverseLayout = true) {
            stickyHeader {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
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
            vm.conversation.forEach { request ->
                items(items = request.replyList, key = { it.id }) {
                    BotReply(text = it.text)
                }
                item {
                    Spacer(modifier = Modifier.padding(4.dp))
                }
                item {
                    UserRequest(text = request.text)
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
fun BotReply(
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
