@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class, ExperimentalPermissionsApi::class)

package de.yanos.islam.ui.settings.localization

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import de.yanos.core.ui.view.CustomDialog
import de.yanos.islam.R
import de.yanos.islam.util.constants.NavigationAction
import de.yanos.islam.util.helper.LocationPermission
import de.yanos.islam.util.helper.headlineMedium
import de.yanos.islam.util.helper.labelLarge
import de.yanos.islam.util.helper.labelSmall

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
@Preview
fun LocalizationSettingView(
    modifier: Modifier = Modifier,
    vm: LocalizationSettingViewModel = hiltViewModel(),
    onNavigationChange: (NavigationAction) -> Unit = {}
) {
    val activity = LocalContext.current as Activity
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val locationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    var showLocationPermission by remember { mutableStateOf(true) }
    var showExplainer by remember { mutableStateOf(false) }
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.settings_section_localization), style = headlineMedium()) },
                navigationIcon = {
                    IconButton(onClick = { onNavigationChange(NavigationAction.NavigateBack) }) {
                        Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "BackNavigation")
                    }
                }
            )
        }
    ) { innerPadding ->
        val launchSettings = {
            if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))
                activity.startActivity(settingsIntent(activity))
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            verticalArrangement = Arrangement.Bottom
        ) {
            when {
                showLocationPermission && !showExplainer -> {
                    LocationPermission(
                        onPermissionDenied = {
                            showLocationPermission = false
                            showExplainer = true
                        },
                        onPermissionGranted = {
                            showLocationPermission = false
                        }
                    )
                }

                showExplainer && !showLocationPermission -> CustomDialog(
                    modifier = modifier,
                    title = stringResource(id = R.string.settings_localization_permission),
                    text = stringResource(id = R.string.settings_localization_explainer),
                    onConfirm = {
                        launchSettings()
                        showExplainer = false
                    },
                    onDismiss = { showExplainer = false },
                    showCancel = true
                )

                !showExplainer && !showLocationPermission -> {
                    if (locationPermissionState.status.isGranted) {
                        LocalizationMap(modifier = Modifier.weight(1f))
                    } else if (!showLocationPermission) {
                        TextButton(onClick = { showLocationPermission = true }) {
                            Text(modifier = Modifier.padding(16.dp), text = stringResource(id = R.string.settings_localization_permission), style = labelLarge())
                        }
                    }
                    AddressField(modifier = Modifier.padding())
                }
            }
        }
    }
}

private fun settingsIntent(activity: Activity): Intent {
    return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", activity.packageName, null)
        addCategory(Intent.CATEGORY_DEFAULT)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
    }
}

@SuppressLint("MissingPermission")
@Composable
@Preview
private fun LocalizationMap(
    modifier: Modifier = Modifier
) {
    val properties by remember {
        mutableStateOf(MapProperties(isMyLocationEnabled = true, mapType = MapType.HYBRID))
    }
    var deviceLatLng by remember { mutableStateOf(LatLng(21.422510, 39.826168)) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(deviceLatLng, 10f)
    }
    GoogleMap(
        modifier = modifier,
        properties = properties,
        cameraPositionState = cameraPositionState,
        onMapClick = { deviceLatLng = it },
        onMyLocationClick = { deviceLatLng = LatLng(it.latitude, it.longitude) }
    ) {
        Marker(
            state = MarkerState(position = deviceLatLng),
            title = "Singapore",
            snippet = "Marker in Singapore"
        )
    }
}

@Composable
@Preview
private fun AddressField(
    modifier: Modifier = Modifier,
    address: String = "",
    onAddressChange: (String) -> Unit = {}
) {
    TextField(
        modifier = modifier.fillMaxWidth(),
        leadingIcon = { Icon(imageVector = Icons.Rounded.LocationCity, contentDescription = "Name Icon") },
        maxLines = 1,
        keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Words, keyboardType = KeyboardType.Text),
        value = address,
        onValueChange = onAddressChange,
        label = { Text(text = stringResource(id = R.string.settings_localization_address), style = labelSmall()) }
    )
}
