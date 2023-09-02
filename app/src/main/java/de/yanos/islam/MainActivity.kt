package de.yanos.islam

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import de.yanos.core.ui.theme.AppTheme
import de.yanos.core.ui.view.DynamicNavigationScreen
import de.yanos.islam.ui.prayer.PrayerScreen
import de.yanos.islam.ui.settings.SettingsScreen
import de.yanos.islam.util.AppSettings
import de.yanos.islam.util.KnowledgeNavigation
import de.yanos.islam.util.MainNavigation
import de.yanos.islam.util.NavigationAction
import de.yanos.islam.util.PatternedBackgroung
import de.yanos.islam.util.Permission
import de.yanos.islam.util.allKnowledge
import de.yanos.islam.util.getUserLocation
import de.yanos.islam.util.typoByConfig
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()
    @Inject lateinit var appSettings: AppSettings
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            vm.onCurrentLocationChanged(getUserLocation(context = LocalContext.current))
            Permission()
            AppTheme(activity = this, typography = typoByConfig(appSettings)) { modifier, config ->
                val navController = rememberNavController()
                DynamicNavigationScreen(
                    modifier = modifier.padding(top = 48.dp), // TODO: Check statusbar problem
                    config = config,
                    destinations = MainNavigation.all,
                    navController = navController
                ) { contentModifier ->
                    //NavHost Here
                    IslamNavHost(
                        modifier = contentModifier,
                        startRoute = MainNavigation.all.first().route,
                        navController = navController
                    )
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
            composable(route = MainNavigation.Praying.route) {
                PrayerScreen()
            }
            composable(route = MainNavigation.Quran.route) {

            }
            composable(route = MainNavigation.Settings.route) {
                SettingsScreen()
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