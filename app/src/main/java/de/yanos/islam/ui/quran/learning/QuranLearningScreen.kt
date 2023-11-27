package de.yanos.islam.ui.quran.learning

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.util.NavigationAction

@Composable
fun QuranLearningScreen(
    modifier: Modifier = Modifier,
    vm: QuranLearningViewModel = hiltViewModel(),
    onNavigationChange: (NavigationAction) -> Unit
) {

}