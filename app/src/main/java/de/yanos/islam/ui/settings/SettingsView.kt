package de.yanos.islam.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.R
import de.yanos.islam.util.Lottie

@Composable
fun SettingsScreen(modifier: Modifier = Modifier, vm: SettingsViewModel = hiltViewModel()) {
    Column {
        Lottie(modifier = Modifier.height(160.dp), resId = R.raw.lottie_config, applyColor = false)
    }
}