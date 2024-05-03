@file:OptIn(ExperimentalMaterial3Api::class)

package de.yanos.islam.ui.settings.main

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Help
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Cached
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PinDrop
import androidx.compose.material.icons.rounded.Subscriptions
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.R
import de.yanos.islam.util.constants.NavigationAction
import de.yanos.islam.util.constants.SettingsNavigationAction
import de.yanos.islam.util.helper.IslamLightDivider
import de.yanos.islam.util.helper.headlineMedium
import de.yanos.islam.util.helper.labelLarge
import de.yanos.islam.util.helper.labelMedium
import de.yanos.islam.util.helper.labelSmall

@Composable
@Preview
fun MainSettingsView(
    modifier: Modifier = Modifier,
    onNavigationChange: (NavigationAction) -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { TopAppBar(title = { Text(text = stringResource(id = R.string.settings_title), style = headlineMedium()) }) }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            item {
                SettingItem(
                    icon = Icons.Rounded.Person,
                    title = R.string.settings_section_profile,
                    description = R.string.settings_section_profile_description,
                    action = SettingsNavigationAction.NavigateToProfile,
                    onNavigationChange = onNavigationChange
                )

            }
            item {
                SettingItem(
                    icon = Icons.Rounded.PinDrop,
                    title = R.string.settings_section_localization,
                    description = R.string.settings_section_localization_description,
                    action = SettingsNavigationAction.NavigateToLocalization,
                    onNavigationChange = onNavigationChange
                )
            }
            item {
                SettingItem(
                    icon = Icons.Rounded.Timer,
                    title = R.string.settings_section_prayer,
                    description = R.string.settings_section_prayer_description,
                    action = SettingsNavigationAction.NavigateToPrayer,
                    onNavigationChange = onNavigationChange
                )
            }
            item {
                SettingItem(
                    icon = Icons.Rounded.Cached,
                    title = R.string.settings_section_cache,
                    description = R.string.settings_section_cache_description,
                    action = SettingsNavigationAction.NavigateToCache,
                    onNavigationChange = onNavigationChange
                )
            }
            item {
                SettingItem(
                    icon = Icons.Rounded.Subscriptions,
                    title = R.string.settings_section_subscription,
                    description = R.string.settings_section_subscription_description,
                    action = SettingsNavigationAction.NavigateToSubscription,
                    onNavigationChange = onNavigationChange
                )
            }
            item {
                SettingItem(
                    icon = Icons.AutoMirrored.Rounded.Help,
                    title = R.string.settings_section_info,
                    description = R.string.settings_section_info_description,
                    action = SettingsNavigationAction.NavigateToInfo,
                    onNavigationChange = onNavigationChange
                )
            }
        }
    }
}

@Composable
@Preview
private fun SettingItem(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.AutoMirrored.Rounded.MenuBook,
    @StringRes title: Int = R.string.settings_section_profile,
    @StringRes description: Int = R.string.settings_section_profile_description,
    action: SettingsNavigationAction = SettingsNavigationAction.NavigateToProfile,
    onNavigationChange: (NavigationAction) -> Unit = {}
) {
    Column(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigationChange(action) }, horizontalArrangement = Arrangement.Start
        ) {
            Icon(imageVector = icon, contentDescription = "Menu Icon")
            Spacer(modifier = Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.Start) {
                Text(text = stringResource(id = title), style = labelLarge())
                Spacer(modifier = Modifier.height(2.dp))
                Text(modifier = Modifier.alpha(0.6f), text = stringResource(id = description), style = labelSmall())
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        IslamLightDivider()
        Spacer(modifier = Modifier.height(8.dp))
    }
}