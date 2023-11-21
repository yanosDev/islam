@file:OptIn(ExperimentalPermissionsApi::class, ExperimentalPermissionsApi::class)

package de.yanos.islam

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.AndroidEntryPoint
import de.yanos.core.ui.theme.AppTheme
import de.yanos.core.ui.view.DynamicNavigationScreen
import de.yanos.islam.ui.permissions.InitScreen
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
import de.yanos.islam.util.typoByConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()
    @Inject lateinit var appSettings: AppSettings
    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            val locationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
            val notificationPermissionState = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

            AppTheme(activity = this, typography = typoByConfig(appSettings)) { modifier, config ->
                if (locationPermissionState.status.isGranted && notificationPermissionState.status.isGranted) {
                    navController = rememberNavController()
                    DynamicNavigationScreen(
                        modifier = modifier.padding(top = 48.dp), // TODO: Check statusbar problem
                        config = config.copy(),
                        destinations = MainNavigation.all,
                        navController = navController!!
                    ) { contentModifier ->
                        IslamNavHost(
                            modifier = contentModifier,
                            startRoute = MainNavigation.all[0].route,
                            navController = navController!!
                        )
                    }
                } else InitScreen(modifier, locationPermissionState, notificationPermissionState)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        vm.startSchedule()
    }

    override fun onPause() {
        vm.cancelSchedule()
        super.onPause()
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
            enterTransition = { fadeIn(animationSpec = tween(200)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) },

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