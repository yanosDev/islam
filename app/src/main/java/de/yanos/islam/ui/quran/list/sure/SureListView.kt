package de.yanos.islam.ui.quran.list.sure

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.R
import de.yanos.islam.util.IslamDivider
import de.yanos.islam.util.NavigationAction
import de.yanos.islam.util.QuranNavigationAction
import de.yanos.islam.util.bodyMedium
import de.yanos.islam.util.bodySmall
import de.yanos.islam.util.labelLarge

@Composable
fun SureListScreen(
    modifier: Modifier = Modifier,
    vm: SureListViewModel = hiltViewModel(),
    onNavigationChange: (NavigationAction) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        items(items = vm.sureList, key = { it.sureaditr }) { detail ->
            ElevatedCard(
                modifier = Modifier
                    .clickable { onNavigationChange(QuranNavigationAction.NavigateToSure(detail.sureaditr)) }
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), text = "${detail.sureaditr} - ${detail.sureadiar}", style = labelLarge())
                IslamDivider()
                Row(modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    Text(text = stringResource(id = R.string.sure_list_cuz, detail.cuz), style = bodyMedium())
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(id = R.string.sure_list_yer, detail.yer), style = bodyMedium())
                }
                IslamDivider()
                Text(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp), text = detail.sureaciklama, style = bodySmall())
            }
        }
    }
}