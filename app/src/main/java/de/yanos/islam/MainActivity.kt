package de.yanos.islam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Notes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.yanos.core.ui.theme.AppTheme
import de.yanos.core.ui.view.DynamicNavigationScreen
import de.yanos.core.utils.NavigationDestination
import de.yanos.islam.ui.TopicView

class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme(activity = this) { modifier, config ->
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
                modifier = Modifier.verticalScroll(rememberScrollState()),
                onTopicSelected = { id: String -> }
            )
        }
    }
}

object Routes {
    internal const val TOPICS = "Topics"
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