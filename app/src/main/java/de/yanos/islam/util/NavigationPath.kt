package de.yanos.islam.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.LockClock
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Topic
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import de.yanos.core.utils.NavigationDestination
import de.yanos.islam.R
import de.yanos.islam.ui.knowledge.challenge.create.ChallengeScreen
import de.yanos.islam.ui.knowledge.challenge.open.OpenChallengesScreen
import de.yanos.islam.ui.knowledge.challenge.session.ChallengeSessionScreen
import de.yanos.islam.ui.knowledge.topics.list.QuestionListScreen
import de.yanos.islam.ui.knowledge.topics.main.MainTopicsScreen
import de.yanos.islam.ui.knowledge.topics.sub.SubTopicsScreen

abstract interface NavigationPath {
    val route: String
    val args: List<NamedNavArgument>

    @Composable
    fun View(onNavigationChange: (NavigationAction) -> Unit) {
    }
}

sealed interface NavigationAction {
    val route: String

    object NavigateBack : NavigationAction {
        override val route: String
            get() = "back"
    }
}

sealed class KnowledgeNavigationAction(override val route: String) : NavigationAction {
    data class NavigateToChallenge(val id: Int) : KnowledgeNavigationAction(KnowledgeNavigation.ChallengeSession.route.replace("{id}", id.toString()))
    object NavigateToOpenChallenges : KnowledgeNavigationAction(KnowledgeNavigation.ChallengeOpen.route)
    data class NavigateToSubTopic(val id: Int) : KnowledgeNavigationAction(KnowledgeNavigation.SubList.route.replace("{id}", id.toString()))
    data class NavigateToTopicQuestions(val id: Int, val parentId: Int?) :
        KnowledgeNavigationAction(KnowledgeNavigation.QuestionList.route.replace("{id}", id.toString()).replace("{parentId}", (parentId?.toString() ?: "-1")))

}

sealed class MainNavigation(override val route: String, override val args: List<NamedNavArgument> = listOf()) : NavigationPath {
    object Knowledge : MainNavigation("knowledge")
    object Quran : MainNavigation("quran")
    object Praying : MainNavigation("praying")
    object Settings : MainNavigation("settings")
    companion object {
        val all = listOf(
            NavigationDestination.TopDestination(
                route = Knowledge.route,
                selectedIcon = Icons.Rounded.Topic,
                unselectedIcon = Icons.Rounded.Topic,
                iconTextId = R.string.tab_topic
            ),
            NavigationDestination.TopDestination(
                route = Quran.route,
                selectedIcon = Icons.Rounded.Book,
                unselectedIcon = Icons.Rounded.Book,
                iconTextId = R.string.tab_quran
            ),
            NavigationDestination.TopDestination(
                route = Praying.route,
                selectedIcon = Icons.Rounded.LockClock,
                unselectedIcon = Icons.Rounded.LockClock,
                iconTextId = R.string.tab_time
            ),
            NavigationDestination.TopDestination(
                route = Settings.route,
                selectedIcon = Icons.Rounded.Settings,
                unselectedIcon = Icons.Rounded.Settings,
                iconTextId = R.string.tab_setting
            )
        )
    }
}

sealed class KnowledgeNavigation(override val route: String, override val args: List<NamedNavArgument> = emptyList()) : NavigationPath {
    object MainList : KnowledgeNavigation("topics") {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            MainTopicsScreen(onNavigationChange = onNavigationChange)
        }
    }

    object SubList : KnowledgeNavigation("topics/{id}", args = listOf(navArgument("id") { type = NavType.IntType })) {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            SubTopicsScreen(onNavigationChange = onNavigationChange)
        }
    }

    object QuestionList :
        KnowledgeNavigation("topics/{parentId}/questions/{id}", args = listOf(navArgument("id") { type = NavType.IntType }, navArgument("parentId") { type = NavType.IntType })) {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            QuestionListScreen()
        }
    }

    object Challenge : KnowledgeNavigation("topics/challenge") {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            ChallengeScreen(onNavigationChange = onNavigationChange)
        }
    }

    object ChallengeSession : KnowledgeNavigation("topic/challenge/{id}", args = listOf(navArgument("id") { type = NavType.IntType })) {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            ChallengeSessionScreen(onNavigationChange = onNavigationChange)
        }
    }

    object ChallengeOpen : KnowledgeNavigation("topic/challenge/open/") {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            OpenChallengesScreen(onNavigationChange = onNavigationChange)
        }
    }

    object ChallengeHistory : KnowledgeNavigation("topic/challenge/history/") {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {

        }
    }

    companion object {
        val all = listOf(MainList, SubList, QuestionList, Challenge, ChallengeSession, ChallengeOpen, ChallengeHistory)
    }
}