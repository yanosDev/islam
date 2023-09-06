package de.yanos.islam

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import dagger.hilt.android.AndroidEntryPoint
import de.yanos.core.ui.theme.AppTheme
import de.yanos.core.ui.view.DynamicNavigationScreen
import de.yanos.islam.ui.permissions.PermissionsScreen
import de.yanos.islam.ui.prayer.PrayerScreen
import de.yanos.islam.ui.settings.SettingsScreen
import de.yanos.islam.util.AppSettings
import de.yanos.islam.util.KnowledgeNavigation
import de.yanos.islam.util.MainNavigation
import de.yanos.islam.util.NavigationAction
import de.yanos.islam.util.PatternedBackgroung
import de.yanos.islam.util.QuranNavigation
import de.yanos.islam.util.ToRootAfterPermission
import de.yanos.islam.util.allKnowledge
import de.yanos.islam.util.allQuran
import de.yanos.islam.util.hasLocationPermission
import de.yanos.islam.util.hasNotificationPermission
import de.yanos.islam.util.typoByConfig
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()
    @Inject lateinit var appSettings: AppSettings
    private var navController: NavHostController? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            var permissionsHandled by rememberSaveable { mutableStateOf(hasNotificationPermission(this) && hasLocationPermission(this)) }

            AppTheme(activity = this, typography = typoByConfig(appSettings)) { modifier, config ->
                if (permissionsHandled) {
                    navController = rememberNavController()
                    DynamicNavigationScreen(
                        modifier = modifier.padding(top = 48.dp), // TODO: Check statusbar problem
                        config = config.copy(),
                        destinations = MainNavigation.all,
                        navController = navController!!
                    ) { contentModifier ->
                        IslamNavHost(
                            modifier = contentModifier,
                            startRoute = MainNavigation.all[1].route,
                            navController = navController!!
                        )
                    }
                } else PermissionsScreen {
                    permissionsHandled = true
                }
            }
        }
        setUpSplash()
    }

    private fun setUpSplash() {
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // Check whether the initial data is ready.
                    return if (vm.isReady) {
                        // The content is ready. Start drawing.
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        // The content isn't ready. Suspend.
                        false
                    }
                }
            }
        )
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navController?.handleDeepLink(intent)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun IslamNavHost(
    modifier: Modifier = Modifier,
    startRoute: String,
    navController: NavHostController,
) {
    val scope = rememberCoroutineScope()
    val onNavigationChange = { path: NavigationAction ->
        scope.launch {
            when (path) {
                NavigationAction.NavigateBack -> navController.popBackStack()
                ToRootAfterPermission -> navController.navigate(MainNavigation.Knowledge.route) {
                    popUpTo(MainNavigation.Knowledge.route) {
                        inclusive = true
                    }
                }

                else -> navController.navigate(path.route)
            }
        }
        Unit
    }
    PatternedBackgroung(modifier = modifier) {
        NavHost(
            navController = navController,
            startDestination = startRoute,
        ) {
            navKnowledge(onNavigationChange = onNavigationChange)
            navQuran(onNavigationChange = onNavigationChange)
            composable(
                route = MainNavigation.Praying.route,
                deepLinks = listOf(navDeepLink { uriPattern = "yanos://de.islam/praying" })
            ) {
                PrayerScreen()
            }
            composable(route = MainNavigation.Settings.route) {
                SettingsScreen()
            }
        }
    }
}

fun NavGraphBuilder.navQuran(onNavigationChange: (NavigationAction) -> Unit) {
    navigation(startDestination = QuranNavigation.QuranMainList.route, route = MainNavigation.Quran.route) {
        allQuran.forEach { path ->
            composable(route = path.route, arguments = path.args) {
                path.View(onNavigationChange = onNavigationChange)
            }
        }
    }
}


fun NavGraphBuilder.navKnowledge(onNavigationChange: (NavigationAction) -> Unit) {
    navigation(startDestination = KnowledgeNavigation.MainList.route, route = MainNavigation.Knowledge.route) {
        allKnowledge.forEach { path ->
            composable(route = path.route, arguments = path.args) {
                path.View(onNavigationChange = onNavigationChange)
            }
        }
    }
}