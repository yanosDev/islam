package de.yanos.islam.ui.permissions

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import de.yanos.islam.R
import de.yanos.islam.util.LocationPermission
import de.yanos.islam.util.NotificationPermission
import de.yanos.islam.util.titleLarge
import de.yanos.islam.util.titleSmall


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun PermissionsScreen(
    modifier: Modifier = Modifier,
    onPermissionHandled: () -> Unit
) {
    var showLocationPermission by remember { mutableStateOf(false) }
    var showNotificationPermission by remember { mutableStateOf(false) }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedVisibility(visible = !showLocationPermission && !showNotificationPermission) {
            Text(modifier = Modifier.padding(16.dp), text = stringResource(id = R.string.permission_title), style = titleLarge())
            Text(modifier = Modifier.padding(16.dp), text = stringResource(id = R.string.permission_content), style = titleSmall())
        }
        AnimatedVisibility(visible = showLocationPermission) {
            LocationPermission(onPermissionGranted = { }, onPermissionDenied = onPermissionHandled)
        }
        AnimatedVisibility(visible = showNotificationPermission) {
            NotificationPermission(onPermissionGranted = onPermissionHandled, onPermissionDenied = onPermissionHandled)
        }
    }
}