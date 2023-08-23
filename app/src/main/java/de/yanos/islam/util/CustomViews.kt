package de.yanos.islam.util

import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun IslamDivider() {
    Divider(modifier = Modifier.alpha(0.2f))
}