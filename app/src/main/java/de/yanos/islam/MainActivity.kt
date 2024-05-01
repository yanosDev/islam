@file:OptIn(ExperimentalPermissionsApi::class, ExperimentalPermissionsApi::class)

package de.yanos.islam

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.AndroidEntryPoint
import de.yanos.core.ui.theme.AppTheme
import de.yanos.core.ui.view.DynamicNavigationScreen
import de.yanos.islam.ui.ai.AIScreen
import de.yanos.islam.ui.permissions.InitScreen
import de.yanos.islam.ui.settings.SettingsScreen
import de.yanos.islam.util.settings.AppSettings
import de.yanos.islam.util.constants.KnowledgeNavigation
import de.yanos.islam.util.constants.MainNavigation
import de.yanos.islam.util.constants.NavigationAction
import de.yanos.islam.util.helper.PatternedBackgroung
import de.yanos.islam.util.constants.QuranNavigation
import de.yanos.islam.util.constants.ToRootAfterPermission
import de.yanos.islam.util.constants.allKnowledge
import de.yanos.islam.util.constants.allQuran
import de.yanos.islam.util.constants.typoByConfig
import kotlinx.coroutines.launch
import javax.inject.Inject

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

            if (locationPermissionState.status.isGranted) {
                vm.readLocationData()
            }
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        navController?.handleDeepLink(intent)
    }
}

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
//                PrayerScreen(modifier = Modifier.fillMaxSize())
            }
            composable(
                route = MainNavigation.AI.route,
            ) {
                AIScreen(modifier = Modifier.fillMaxSize())
            }
            composable(
                route = MainNavigation.Settings.route,
            ) {
                SettingsScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

fun NavGraphBuilder.navQuran(onNavigationChange: (NavigationAction) -> Unit) {
    navigation(
        startDestination = QuranNavigation.QuranMainList.route,
        route = MainNavigation.Quran.route,
    ) {
        allQuran.forEachIndexed { index, path ->
            if (index == 0)
                composable(
                    route = path.route, arguments = path.args,
                    exitTransition = { fadeOut() }
                ) {
                    path.View(onNavigationChange = onNavigationChange)
                }
            else
                composable(
                    route = path.route, arguments = path.args,
                    enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
                    exitTransition = { fadeOut() },
                    popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
                    popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) },
                ) {
                    path.View(onNavigationChange = onNavigationChange)
                }
        }
    }
}

fun NavGraphBuilder.navKnowledge(onNavigationChange: (NavigationAction) -> Unit) {
    navigation(
        startDestination = KnowledgeNavigation.MainList.route,
        route = MainNavigation.Knowledge.route,
    ) {
        allKnowledge.forEachIndexed { index, path ->
            if (index == 0)
                composable(
                    route = path.route, arguments = path.args,
                    exitTransition = { fadeOut() }
                ) {
                    path.View(onNavigationChange = onNavigationChange)
                }
            else
                composable(
                    route = path.route, arguments = path.args,
                    enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
                    exitTransition = { fadeOut() },
                    popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
                    popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) },
                ) {
                    path.View(onNavigationChange = onNavigationChange)
                }
        }
    }
}