package de.yanos.islam.ui.quran.list.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.R
import de.yanos.islam.ui.permissions.DownloadingScreen
import de.yanos.islam.util.Lottie
import de.yanos.islam.util.NavigationAction
import de.yanos.islam.util.QuranNavigationAction
import de.yanos.islam.util.goldColor
import de.yanos.islam.util.labelMedium

@Composable
fun QuranMainListScreen(
    modifier: Modifier = Modifier,
    vm: QuranMainListViewModel = hiltViewModel(),
    onNavigationChange: (NavigationAction) -> Unit
) {
    Column(
        modifier = modifier
            .widthIn(320.dp, 600.dp)
            .padding(horizontal = 32.dp, vertical = 24.dp)
            .fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Lottie(modifier = Modifier.height(180.dp), resId = R.raw.lottie_quran2, applyColor = false)
        Spacer(modifier = Modifier.height(12.dp))
        val isEmpty = vm.lastSurah.collectAsState(initial = listOf()).value.isEmpty()

        Column {
            ElevatedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = AbsoluteCutCornerShape(8.dp),
                border = BorderStroke(1.dp, goldColor()),
                onClick = { onNavigationChange(QuranNavigationAction.NavigateToQuran) },
            ) {
                Text(text = stringResource(id = R.string.quran_read_main), style = labelMedium(), color = goldColor())
            }
            ElevatedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                shape = AbsoluteCutCornerShape(8.dp),
                border = BorderStroke(1.dp, goldColor()),
                onClick = { onNavigationChange(QuranNavigationAction.NavigateToSureList) },
            ) {
                Text(text = stringResource(id = R.string.quran_sure_list), style = labelMedium())
            }
            ElevatedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                shape = AbsoluteCutCornerShape(8.dp),
                border = BorderStroke(1.dp, goldColor()),
                onClick = { onNavigationChange(QuranNavigationAction.NavigateToQuranSearch) },
            ) {
                Text(text = stringResource(id = R.string.quran_search), style = labelMedium())
            }
            ElevatedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                shape = AbsoluteCutCornerShape(8.dp),
                border = BorderStroke(1.dp, goldColor()),
                onClick = { onNavigationChange(QuranNavigationAction.NavigateToVideoLearnings) },
            ) {
                Text(text = stringResource(id = R.string.quran_video), style = labelMedium())
            }
        }
        AnimatedVisibility(visible = !vm.isDBInitialized && isEmpty) {
            DownloadingScreen(downloadingResources = !vm.isDBInitialized)
        }
    }

}