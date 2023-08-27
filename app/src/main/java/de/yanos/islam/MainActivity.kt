package de.yanos.islam

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import de.yanos.core.ui.theme.AppTheme
import de.yanos.core.ui.view.DynamicNavigationScreen
import de.yanos.core.utils.NavigationDestination
import de.yanos.islam.ui.questions.main.MainTopicsScreen
import de.yanos.islam.ui.questions.sub.SubTopicsScreen
import de.yanos.islam.ui.quiz.config.QuizConfigurationView
import de.yanos.islam.ui.quiz.session.QuizFormView
import de.yanos.islam.ui.topic.list.SubTopicView
import de.yanos.islam.ui.topic.list.TopicView
import de.yanos.islam.ui.topic.questions.TopicContentView
import de.yanos.islam.util.AlogicalTypography
import de.yanos.islam.util.NAVIGATION_BAR_DESTINATIONS
import de.yanos.islam.util.NavigationPath
import de.yanos.islam.util.PatternedBackgroung
import de.yanos.islam.util.Routes
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme(activity = this, typography = AlogicalTypography) { modifier, config ->
                val navController = rememberNavController()
                DynamicNavigationScreen(
                    modifier = modifier.padding(top = 48.dp), // TODO: Check statusbar problem
                    config = config,
                    destinations = NAVIGATION_BAR_DESTINATIONS,
                    navController = navController
                ) { contentModifier ->
                    //NavHost Here
                    IslamNavHost(
                        modifier = contentModifier,
                        startRoute = Routes.TOPICS,
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
    val navigationHandler = { path: NavigationPath ->
        scope.launch {
            if (path is NavigationPath.NavigateBack)
                navController.popBackStack()
            else
                navController.navigate(path.route)
        }
        Unit
    }

    PatternedBackgroung(modifier = modifier) {
        NavHost(
            navController = navController,
            startDestination = startRoute,
        ) {
            composable(
                route = Routes.MAIN_TOPIC_LIST
            ) {
                MainTopicsScreen(onNavigationChange = navigationHandler)
            }
            composable(route = Routes.CHALLENGE) {

            }
            composable(route = Routes.TIMES) {

            }
            composable(route = Routes.HISTORY) {

            }
            composable(route = Routes.SETTINGS) {

            }
            composable(
                route = Routes.SUB_TOPIC_LIST,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) {
                SubTopicsScreen(onNavigationChange = navigationHandler)
            }
            composable(
                Routes.QUESTION_LIST,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                SubTopicView(
                    onTopicClick = { topic -> navController.navigate(Routes.TOPIC_CONTENT.replace("{id}", topic.id.toString())) },
                    topicId = backStackEntry.arguments?.getInt("id")!!
                )
            }
            composable(
                Routes.TOPIC_CONTENT,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                TopicContentView(
                    topicId = backStackEntry.arguments?.getInt("id")!!
                )
            }
            composable(Routes.QUIZ_CONFIG) {
                QuizConfigurationView { id: Int ->
                    navController.navigate(Routes.QUIZ_PLAY.replace("{id}", id.toString()))
                }
            }
            composable(
                Routes.QUIZ_PLAY,
                arguments = listOf(
                    navArgument("id") { type = NavType.IntType },
                )
            ) { backStackEntry ->
                QuizFormView(id = backStackEntry.arguments?.getInt("id")!!) {
                    navController.popBackStack()
                }
            }
        }
    }
}
