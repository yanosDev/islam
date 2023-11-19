package de.yanos.islam.util

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.LockClock
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Topic
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import de.yanos.islam.ui.knowledge.topics.search.SearchQuestionsScreen
import de.yanos.islam.ui.knowledge.topics.sub.SubTopicsScreen
import de.yanos.islam.ui.quran.classic.QuranClassicScreen
import de.yanos.islam.ui.quran.list.main.QuranMainListScreen
import de.yanos.islam.ui.quran.list.sure.SureListScreen
import de.yanos.islam.ui.quran.partial.QuranPartialScreen
import de.yanos.islam.ui.quran.search.QuranSearchScreen

interface NavigationPath {
    val route: String
    val args: List<NamedNavArgument>

    @Composable
    fun View(onNavigationChange: (NavigationAction) -> Unit) {
        Text(modifier = Modifier.fillMaxSize(), text = "Coming Soon", style = headlineLarge())
    }
}

sealed interface NavigationAction {
    val route: String

    object NavigateBack : NavigationAction {
        override val route: String
            get() = "back"
    }
}

object ToRootAfterPermission : NavigationAction {
    override val route: String = KnowledgeNavigation.MainList.route
}

sealed class QuranNavigationAction(override val route: String) : NavigationAction {
    object NavigateToQuran : QuranNavigationAction(QuranNavigation.QuranClassic.route)
    object NavigateToQuranSearch : QuranNavigationAction(QuranNavigation.SearchThroughQuran.route)
    object NavigateToSureList : QuranNavigationAction(QuranNavigation.SureList.route)
    class NavigateToSure(val name: String) : QuranNavigationAction(QuranNavigation.QuranPartial.route.replace("{name}", name))
}

sealed class KnowledgeNavigationAction(override val route: String) : NavigationAction {
    object NavigateToChallengeCreation : KnowledgeNavigationAction(KnowledgeNavigation.Challenge.route)
    data class NavigateToChallenge(val id: Int) : KnowledgeNavigationAction(KnowledgeNavigation.ChallengeSession.route.replace("{id}", id.toString()))
    object NavigateToOpenChallenges : KnowledgeNavigationAction(KnowledgeNavigation.ChallengeOpen.route)
    object NavigateToSearchQuestions : KnowledgeNavigationAction(KnowledgeNavigation.SearchQuestions.route)
    data class NavigateToSubTopic(val id: Int) : KnowledgeNavigationAction(KnowledgeNavigation.SubList.route.replace("{id}", id.toString()))
    data class NavigateToTopicQuestions(val id: Int, val parentId: Int?) :
        KnowledgeNavigationAction(KnowledgeNavigation.QuestionList.route.replace("{id}", id.toString()).replace("{parentId}", (parentId?.toString() ?: "-1")))

}

object Permissions : NavigationPath {
    override val route: String = "permissions/notification"
    override val args: List<NamedNavArgument> = emptyList()
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

sealed class QuranNavigation(override val route: String, override val args: List<NamedNavArgument> = emptyList()) : NavigationPath {

    object QuranMainList : QuranNavigation("quran/main") {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            QuranMainListScreen(onNavigationChange = onNavigationChange)
        }
    }

    object SureList : QuranNavigation("quran/sure") {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            SureListScreen(onNavigationChange = onNavigationChange)
        }
    }

    object QuranPartial : QuranNavigation("quran/sure/{name}", args = listOf(navArgument("name") { type = NavType.StringType })) {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            QuranPartialScreen(onNavigationChange = onNavigationChange)
        }
    }

    object QuranClassic : QuranNavigation("quran/book") {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            QuranClassicScreen(onNavigationChange = onNavigationChange)
        }
    }

    object SearchThroughQuran : QuranNavigation("quran/search/") {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            QuranSearchScreen(onNavigationChange = onNavigationChange)
        }
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

    object ChallengeSession : KnowledgeNavigation("topics/challenge/{id}", args = listOf(navArgument("id") { type = NavType.IntType })) {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            ChallengeSessionScreen(onNavigationChange = onNavigationChange)
        }
    }

    object ChallengeOpen : KnowledgeNavigation("topics/challenge/open/") {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            OpenChallengesScreen(onNavigationChange = onNavigationChange)
        }
    }

    object SearchQuestions : KnowledgeNavigation("topics/search/") {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            SearchQuestionsScreen(onNavigationChange = onNavigationChange)
        }
    }

    object ChallengeHistory : KnowledgeNavigation("topics/challenge/history/")
}

val allKnowledge = listOf(
    KnowledgeNavigation.MainList,
    KnowledgeNavigation.SubList,
    KnowledgeNavigation.QuestionList,
    KnowledgeNavigation.SearchQuestions,
    KnowledgeNavigation.Challenge,
    KnowledgeNavigation.ChallengeSession,
    KnowledgeNavigation.ChallengeOpen,
    KnowledgeNavigation.ChallengeHistory
)

val allQuran = listOf(
    QuranNavigation.QuranMainList,
    QuranNavigation.SureList,
    QuranNavigation.QuranPartial,
    QuranNavigation.QuranClassic,
    QuranNavigation.SearchThroughQuran,
)