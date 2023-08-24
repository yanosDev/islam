package de.yanos.islam

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Notes
import androidx.compose.runtime.Composable
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
import de.yanos.islam.ui.topic.SubTopicView
import de.yanos.islam.ui.topic.TopicView
import de.yanos.islam.ui.topic.content.TopicContentView
import de.yanos.islam.util.KhodjahTypography
import de.yanos.islam.util.SabanaTypography
import de.yanos.islam.util.SirajunTypography

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme(activity = this, typography = SabanaTypography) { modifier, config ->
                val navController = rememberNavController()
                DynamicNavigationScreen(
                    modifier = modifier.padding(top = 48.dp), // TODO: Check statusbar problem
                    config = config,
                    destinations = ISLAM_DESTINATIONS,
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

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startRoute,
    ) {
        composable(Routes.TOPICS) {
            TopicView(
                onOpenQuizByTopic = { topic ->
                    if (topic.hasSubTopics)
                        navController.navigate(Routes.SUBTOPICS.replace("{id}", topic.id.toString()))
                    else
                        navController.navigate(Routes.TOPIC_CONTENT.replace("{id}", topic.id.toString()))
                }
            )
        }
        composable(
            Routes.SUBTOPICS,
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
    }
}

object Routes {
    internal const val TOPICS = "Topics"
    internal const val SUBTOPICS = "SubTopics/{id}"
    internal const val TOPIC_CONTENT = "TopicContent/{id}"
    internal const val QUIZ = "Quiz"
}

private val ISLAM_DESTINATIONS = listOf(
    NavigationDestination.TopDestination(
        route = Routes.TOPICS,
        selectedIcon = Icons.Default.Inbox,
        unselectedIcon = Icons.Default.Inbox,
        iconTextId = R.string.tab_topic
    ),
    NavigationDestination.TopDestination(
        route = Routes.QUIZ,
        selectedIcon = Icons.Default.Notes,
        unselectedIcon = Icons.Default.Notes,
        iconTextId = R.string.tab_quiz
    ),
)