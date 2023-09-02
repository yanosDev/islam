package de.yanos.islam.ui.knowledge.topics.search

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.R
import de.yanos.islam.data.model.Search
import de.yanos.islam.util.IslamDivider
import de.yanos.islam.util.NavigationAction
import de.yanos.islam.util.bodyMedium
import de.yanos.islam.util.bodySmall
import de.yanos.islam.util.goldColor
import de.yanos.islam.util.labelLarge
import de.yanos.islam.util.labelMedium

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SearchQuestionsScreen(
    modifier: Modifier = Modifier,
    vm: SearchQuestionsViewModel = hiltViewModel(),
    onNavigationChange: (path: NavigationAction) -> Unit = {}
) {
    var isActive by remember { mutableStateOf(false) }
    val recentSearches = vm.recentSearches.collectAsState(initial = listOf()).value
    Scaffold(
        modifier = modifier.fillMaxSize().apply {
            if (!isActive)
                padding(horizontal = 12.dp)
        },
    ) { values ->
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            SearchBar(
                query = vm.query,
                onQueryChange = { vm.search(it) },
                onSearch = {
                    isActive = false
                    vm.search(it, true)
                },
                active = isActive,
                onActiveChange = { isActive = !isActive },
                colors = SearchBarDefaults.colors(dividerColor = SearchBarDefaults.colors().dividerColor.copy(alpha = 0.25f)),
                placeholder = {
                    Text(modifier = Modifier.alpha(0.25f), text = stringResource(id = R.string.search_placeholder), style = labelMedium())
                },
                leadingIcon = { Icon(modifier = Modifier.alpha(0.25f), imageVector = Icons.Rounded.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (isActive && vm.query.isNotBlank())
                        IconButton(onClick = {
                            isActive = false
                            vm.clearSearch()
                        }) {
                            Icon(modifier = Modifier.alpha(0.25f), imageVector = Icons.Rounded.Clear, contentDescription = "Search")
                        }
                }
            ) {
                LazyColumn {
                    items(count = (recentSearches.takeIf { isActive } ?: listOf()).size) { index ->
                        SearchHistory(modifier = Modifier.fillMaxWidth(), search = recentSearches[index]) { query ->
                            isActive = false
                            vm.search(query, false)
                        }
                    }
                    items(items = vm.findings, key = { it.id }) {
                        MatchingQuestions(
                            modifier = Modifier.animateItemPlacement(),
                            question = vm.getAnnotatedString("${it.question}?", SpanStyle(color = goldColor(), fontWeight = FontWeight.Bold)),
                            answer = vm.getAnnotatedString(it.answer, SpanStyle(color = goldColor(), fontWeight = FontWeight.Bold)),
                        )
                    }
                }

            }
            LazyColumn {
                items(items = vm.findings, key = { it.id }) {
                    MatchingQuestions(
                        modifier = Modifier.animateItemPlacement(),
                        question = vm.getAnnotatedString("${it.question}?", SpanStyle(color = goldColor(), fontWeight = FontWeight.Bold)),
                        answer = vm.getAnnotatedString(it.answer, SpanStyle(color = goldColor(), fontWeight = FontWeight.Bold)),
                    )
                }
            }
        }
    }
}

@Composable
fun SearchHistory(modifier: Modifier = Modifier, search: Search, onHistoryClicked: (String) -> Unit) {
    Row(modifier = Modifier
        .padding(horizontal = 18.dp, vertical = 6.dp)
        .clickable { onHistoryClicked(search.query) }) {
        Icon(imageVector = Icons.Rounded.History, contentDescription = "Search History")
        Spacer(modifier = Modifier.width(6.dp))
        Text(modifier = Modifier.weight(1f), textAlign = TextAlign.Start, text = search.query, style = bodySmall())
    }
}

@Composable
private fun MatchingQuestions(modifier: Modifier, question: AnnotatedString, answer: AnnotatedString) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp),
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(modifier = Modifier.padding(horizontal = 8.dp), text = question, style = labelLarge())
        Spacer(modifier = Modifier.height(8.dp))
        IslamDivider()
        Spacer(modifier = Modifier.height(8.dp))
        Text(modifier = Modifier.padding(horizontal = 8.dp), text = answer, style = bodyMedium())
        Spacer(modifier = Modifier.height(12.dp))
    }
}