package de.yanos.islam.util.helper

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RawRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.FactCheck
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import de.yanos.islam.R
import de.yanos.islam.util.states.ScreenState


@Composable
@Preview
fun IslamDivider(modifier: Modifier = Modifier, color: Color = goldColor()) {
    HorizontalDivider(modifier = modifier, color = color)
}

@Composable
@Preview
fun IslamLightDivider(modifier: Modifier = Modifier, color: Color = goldColor()) {
    HorizontalDivider(modifier = modifier.alpha(0.25f), color = color)
}

@Composable
fun IslamCheckBox(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isChecked: Boolean,
    onCheckChange: (Boolean) -> Unit,
    content: @Composable () -> Unit,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
        Checkbox(
            enabled = isEnabled,
            checked = isChecked,
            onCheckedChange = onCheckChange
        )
        TextButton(onClick = { onCheckChange(!isChecked) }, enabled = isEnabled) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IslamDropDown(
    selectedValue: String,
    options: List<String>,
    label: String,
    onValueChangedEvent: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedValue,
            onValueChange = {},
            label = { Text(text = label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option: String ->
                DropdownMenuItem(
                    text = { Text(text = option) },
                    trailingIcon = { if (selectedValue != option) Icons.AutoMirrored.Rounded.FactCheck },
                    onClick = {
                        expanded = false
                        onValueChangedEvent(option)
                    }
                )
            }
        }
    }
}

@Composable
fun IslamSwitch(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isChecked: Boolean,
    onCheckChange: (Boolean) -> Unit,
    content: @Composable () -> Unit,
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        TextButton(onClick = { onCheckChange(!isChecked) }, enabled = isEnabled) {
            content()
        }
        Switch(
            enabled = isEnabled,
            checked = isChecked,
            onCheckedChange = onCheckChange
        )
    }
}

@Composable
fun IslamRadio(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    @StringRes text: Int,
    onClick: () -> Unit
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
        RadioButton(
            selected = isSelected,
            onClick = onClick
        )
        TextButton(onClick = onClick) {
            Text(modifier = Modifier, textAlign = TextAlign.Start, text = stringResource(id = text), style = labelSmall())
        }
    }
}

@Composable
fun Lottie(modifier: Modifier, @RawRes resId: Int, applyColor: Boolean = true) {
    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR_FILTER,
            value = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                MaterialTheme.colorScheme.primary.hashCode(),
                BlendModeCompat.SRC_ATOP
            ),
            keyPath = arrayOf(
                "**"
            )
        )
    )
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(resId))
    if (applyColor)
        LottieAnimation(
            modifier = modifier,
            composition = composition,
            iterations = LottieConstants.IterateForever,
            dynamicProperties = dynamicProperties,
        )
    else
        LottieAnimation(
            modifier = modifier,
            composition = composition,
            iterations = LottieConstants.IterateForever
        )

}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NotificationPermission(
    onPermissionGranted: () -> Unit = {},
    onPermissionDenied: () -> Unit = {}
) {
    PermissionCompose(
        permission = Manifest.permission.POST_NOTIFICATIONS,
        onPermissionGranted = onPermissionGranted,
        onPermissionDenied = onPermissionDenied
    )
}

@Composable
fun LocationPermission(
    onPermissionGranted: () -> Unit = {},
    onPermissionDenied: () -> Unit = {}
) {
    PermissionCompose(
        permission = Manifest.permission.ACCESS_FINE_LOCATION,
        onPermissionGranted = onPermissionGranted,
        onPermissionDenied = onPermissionDenied
    )
}

@Composable
fun PermissionCompose(
    permission: String,
    onPermissionGranted: () -> Unit = {},
    onPermissionDenied: () -> Unit = {}
) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    val context = LocalContext.current
    if (ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        onPermissionGranted()
    } else {
        LaunchedEffect(key1 = true) {
            launcher.launch(permission)
        }
    }
}

@Composable
fun QuranText(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        SelectionContainer {
            content()
        }
    }
}
