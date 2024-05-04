package de.yanos.islam.util.constants

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.LockClock
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
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
import de.yanos.islam.ui.quran.learning.QuranLearningScreen
import de.yanos.islam.ui.quran.list.main.QuranMainListScreen
import de.yanos.islam.ui.quran.list.sure.SureListScreen
import de.yanos.islam.ui.quran.partial.QuranPartialScreen
import de.yanos.islam.ui.quran.search.QuranSearchScreen
import de.yanos.islam.ui.settings.font.FontSettingView
import de.yanos.islam.ui.settings.info.InfoSettingView
import de.yanos.islam.ui.settings.language.ProfileSettingView
import de.yanos.islam.ui.settings.localization.LocalizationSettingView
import de.yanos.islam.ui.settings.main.MainSettingsView
import de.yanos.islam.ui.settings.prayer.PrayerSettingView
import de.yanos.islam.ui.settings.subscription.SubscriptionSettingView
import de.yanos.islam.util.helper.headlineLarge

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

data object ToRootAfterPermission : NavigationAction {
    override val route: String = KnowledgeNavigation.MainList.route
}

sealed class QuranNavigationAction(override val route: String) : NavigationAction {
    data object NavigateToQuran : QuranNavigationAction(QuranNavigation.QuranClassic.route)
    data object NavigateToQuranSearch : QuranNavigationAction(QuranNavigation.SearchThroughQuran.route)
    data object NavigateToVideoLearnings : QuranNavigationAction(QuranNavigation.QuranLearning.route)
    data object NavigateToSureList : QuranNavigationAction(QuranNavigation.SureList.route)
    class NavigateToSure(val id: Int) : QuranNavigationAction(QuranNavigation.QuranPartial.route.replace("{id}", id.toString()))
}

sealed class SettingsNavigationAction(override val route: String) : NavigationAction {
    data object NavigateToProfile : SettingsNavigationAction(SettingsNavigation.ProfileSetting.route)
    data object NavigateToLocalization : SettingsNavigationAction(SettingsNavigation.LocalizationSetting.route)
    data object NavigateToPrayer : SettingsNavigationAction(SettingsNavigation.PrayerSetting.route)
    data object NavigateToSubscription : SettingsNavigationAction(SettingsNavigation.SubscriptionSetting.route)
    data object NavigateToInfo : SettingsNavigationAction(SettingsNavigation.InfoSetting.route)
    data object NavigateToCache : SettingsNavigationAction(SettingsNavigation.CacheSetting.route)
}

sealed class KnowledgeNavigationAction(override val route: String) : NavigationAction {
    data object NavigateToChallengeCreation : KnowledgeNavigationAction(KnowledgeNavigation.Challenge.route)
    data class NavigateToChallenge(val id: Int) : KnowledgeNavigationAction(KnowledgeNavigation.ChallengeSession.route.replace("{id}", id.toString()))
    data object NavigateToOpenChallenges : KnowledgeNavigationAction(KnowledgeNavigation.ChallengeOpen.route)
    data object NavigateToSearchQuestions : KnowledgeNavigationAction(KnowledgeNavigation.SearchQuestions.route)
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
    object AI : MainNavigation("ai")
    object Settings : MainNavigation("setting")

    companion object {

        val all = listOf(
            NavigationDestination.TopDestination(
                route = Praying.route,
                selectedIcon = Icons.Rounded.LockClock,
                unselectedIcon = Icons.Rounded.LockClock,
                iconTextId = R.string.tab_time
            ),
            NavigationDestination.TopDestination(
                route = Knowledge.route,
                selectedIcon = Icons.Rounded.Search,
                unselectedIcon = Icons.Rounded.Search,
                iconTextId = R.string.tab_topic
            ),
            NavigationDestination.TopDestination(
                route = Quran.route,
                selectedIcon = Icons.AutoMirrored.Rounded.MenuBook,
                unselectedIcon = Icons.AutoMirrored.Rounded.MenuBook,
                iconTextId = R.string.tab_quran
            ),
            NavigationDestination.TopDestination(
                route = AI.route,
                selectedIcon = Icons.AutoMirrored.Rounded.Chat,
                unselectedIcon = Icons.AutoMirrored.Rounded.Chat,
                iconTextId = R.string.tab_ai
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

    data object QuranMainList : QuranNavigation("quran/main") {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            QuranMainListScreen(modifier = Modifier.fillMaxSize(), onNavigationChange = onNavigationChange)
        }
    }

    data object SureList : QuranNavigation("quran/sure") {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            SureListScreen(modifier = Modifier.fillMaxSize(), onNavigationChange = onNavigationChange)
        }
    }

    data object QuranPartial : QuranNavigation("quran/sure/{id}", args = listOf(navArgument("id") { type = NavType.IntType })) {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            QuranPartialScreen(modifier = Modifier.fillMaxSize())
        }
    }

    data object QuranClassic : QuranNavigation("quran/book") {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            QuranClassicScreen(modifier = Modifier.fillMaxSize())
        }
    }

    data object SearchThroughQuran : QuranNavigation("quran/search/") {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            QuranSearchScreen(modifier = Modifier.fillMaxSize(), onNavigationChange = onNavigationChange)
        }
    }

    data object QuranLearning : QuranNavigation("quran/learning/") {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            QuranLearningScreen(modifier = Modifier.fillMaxSize())
        }
    }
}

sealed class SettingsNavigation(override val route: String, override val args: List<NamedNavArgument> = emptyList()) : NavigationPath {
    data object MainSetting : SettingsNavigation("setting/main") {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            MainSettingsView(onNavigationChange = onNavigationChange)
        }
    }

    data object ProfileSetting : SettingsNavigation("setting/profile") {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            ProfileSettingView(onNavigationChange = onNavigationChange)
        }
    }

    data object FontSetting : SettingsNavigation("setting/font") {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            FontSettingView()
        }
    }

    data object InfoSetting : SettingsNavigation("setting/info") {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            InfoSettingView()
        }
    }

    data object CacheSetting : SettingsNavigation("setting/cache") {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            InfoSettingView()
        }
    }

    data object LocalizationSetting : SettingsNavigation("setting/localization") {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            LocalizationSettingView(onNavigationChange = onNavigationChange)
        }
    }

    data object PrayerSetting : SettingsNavigation("setting/prayer") {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            PrayerSettingView()
        }
    }

    data object SubscriptionSetting : SettingsNavigation("setting/subscribe") {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            SubscriptionSettingView()
        }
    }


}

sealed class KnowledgeNavigation(override val route: String, override val args: List<NamedNavArgument> = emptyList()) : NavigationPath {
    data object MainList : KnowledgeNavigation("topics") {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            MainTopicsScreen(modifier = Modifier.fillMaxSize(), onNavigationChange = onNavigationChange)
        }
    }

    data object SubList : KnowledgeNavigation("topics/{id}", args = listOf(navArgument("id") { type = NavType.IntType })) {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            SubTopicsScreen(modifier = Modifier.fillMaxSize(), onNavigationChange = onNavigationChange)
        }
    }

    data object QuestionList :
        KnowledgeNavigation("topics/{parentId}/questions/{id}", args = listOf(navArgument("id") { type = NavType.IntType }, navArgument("parentId") { type = NavType.IntType })) {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            QuestionListScreen(modifier = Modifier.fillMaxSize())
        }
    }

    data object Challenge : KnowledgeNavigation("topics/challenge") {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            ChallengeScreen(modifier = Modifier.fillMaxSize(), onNavigationChange = onNavigationChange)
        }
    }

    data object ChallengeSession : KnowledgeNavigation("topics/challenge/{id}", args = listOf(navArgument("id") { type = NavType.IntType })) {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            ChallengeSessionScreen(modifier = Modifier.fillMaxSize(), onNavigationChange = onNavigationChange)
        }
    }

    data object ChallengeOpen : KnowledgeNavigation("topics/challenge/open/") {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            OpenChallengesScreen(modifier = Modifier.fillMaxSize(), onNavigationChange = onNavigationChange)
        }
    }

    data object SearchQuestions : KnowledgeNavigation("topics/search/") {
        @Composable
        override fun View(onNavigationChange: (NavigationAction) -> Unit) {
            SearchQuestionsScreen(modifier = Modifier.fillMaxSize())
        }
    }

    data object ChallengeHistory : KnowledgeNavigation("topics/challenge/history/")
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
    QuranNavigation.QuranLearning,
)

val allSettings = listOf(
    SettingsNavigation.MainSetting,
    SettingsNavigation.ProfileSetting,
    SettingsNavigation.FontSetting,
    SettingsNavigation.InfoSetting,
    SettingsNavigation.LocalizationSetting,
    SettingsNavigation.PrayerSetting,
    SettingsNavigation.CacheSetting,
    SettingsNavigation.SubscriptionSetting,
)