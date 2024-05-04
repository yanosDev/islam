@file:OptIn(ExperimentalMaterial3Api::class)

package de.yanos.islam.ui.settings.language

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Person
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.R
import de.yanos.islam.util.constants.NavigationAction
import de.yanos.islam.util.helper.bodyLarge
import de.yanos.islam.util.helper.headlineMedium
import de.yanos.islam.util.helper.labelSmall

@Composable
@Preview
fun ProfileSettingView(
    modifier: Modifier = Modifier,
    vm: ProfileSettingViewModel = hiltViewModel(),
    onNavigationChange: (NavigationAction) -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.settings_section_profile), style = headlineMedium()) },
                navigationIcon = {
                    IconButton(onClick = { onNavigationChange(NavigationAction.NavigateBack) }) {
                        Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "BackNavigation")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            item {
                UserHeader(
                    name = vm.name,
                    onNameChange = vm::updateUserName,
                    isLoggedIn = vm.currentUser.collectAsState().value != null,
                    onSignIn = vm::signInAnonymously,
                    onSignOut = vm::signOut
                )
            }
        }
    }
}


@Composable
@Preview
private fun UserHeader(
    modifier: Modifier = Modifier,
    name: String = "",
    isLoggedIn: Boolean = false,
    onNameChange: (name: String) -> Unit = {},
    onSignOut: () -> Unit = {},
    onSignIn: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Start),
            leadingIcon = { Icon(imageVector = Icons.Rounded.Person, contentDescription = "Name Icon") },
            maxLines = 1,
            keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Words, keyboardType = KeyboardType.Text),
            value = name,
            onValueChange = onNameChange,
            label = { Text(text = stringResource(id = R.string.settings_profile_name), style = labelSmall()) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(modifier = Modifier.align(Alignment.End), onClick = { if (isLoggedIn) onSignOut() else onSignIn() }) {
            Text(text = stringResource(id = if (isLoggedIn) R.string.settings_profile_sign_out else R.string.settings_profile_sign_in), style = bodyLarge())
        }
    }
}
