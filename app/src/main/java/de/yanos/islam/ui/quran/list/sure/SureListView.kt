package de.yanos.islam.ui.quran.list.sure

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.R
import de.yanos.islam.util.helper.IslamDivider
import de.yanos.islam.util.helper.IslamRadio
import de.yanos.islam.util.constants.NavigationAction
import de.yanos.islam.util.constants.QuranNavigationAction
import de.yanos.islam.util.helper.bodyMedium
import de.yanos.islam.util.helper.bodySmall
import de.yanos.islam.util.helper.headlineSmall
import de.yanos.islam.util.helper.labelLarge

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SureListScreen(
    modifier: Modifier = Modifier,
    vm: SureListViewModel = hiltViewModel(),
    onNavigationChange: (NavigationAction) -> Unit
) {
    var showSettings by remember { mutableStateOf(false) }
    LazyColumn(
        modifier = modifier
    ) {
        stickyHeader {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.background), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(12.dp), text = stringResource(id = R.string.sure_list_header), style = headlineSmall()
                )
                AnimatedVisibility(visible = showSettings) {
                    Column {
                        SureSorting.values().forEach { sorting ->
                            IslamRadio(isSelected = vm.sortBy == sorting, text = sorting.textId) { vm.onSortChange(sorting) }
                        }
                    }
                }
                IconButton(onClick = { showSettings = !showSettings }) {
                    Icon(
                        modifier = Modifier
                            .height(24.dp)
                            .width(24.dp),
                        imageVector = if (showSettings) Icons.Rounded.ArrowUpward else Icons.Rounded.ArrowDownward,
                        contentDescription = "OpenSettings"
                    )
                }
            }
        }
        items(items = vm.sureList, key = { it.id }) { detail ->
            ElevatedCard(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable { onNavigationChange(QuranNavigationAction.NavigateToSure(detail.id)) }
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), text = "${detail.engName} - ${detail.name}", style = labelLarge())
                IslamDivider()
                Row(modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    Text(text = stringResource(id = R.string.sure_list_cuz, detail.juz), style = bodyMedium())
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(id = R.string.sure_list_yer, detail.revelation), style = bodyMedium())
                }
                IslamDivider()
                Text(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp), text = detail.meaning, style = bodySmall())
            }
        }
    }
}