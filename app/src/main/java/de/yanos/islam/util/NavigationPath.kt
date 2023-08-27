package de.yanos.islam.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.LockClock
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Topic
import de.yanos.core.utils.NavigationDestination
import de.yanos.islam.R

sealed class NavigationPath(val route: String) {
    data class NavigateToChallenge(val id: Int) : NavigationPath(Routes.CHALLENGE_SESSION.replace("{id}", id.toString()))
    data class NavigateToSubTopic(val id: Int) : NavigationPath(Routes.SUB_TOPIC_LIST.replace("{id}", id.toString()))
    data class NavigateToTopicQuestions(val id: Int, val parentId: Int?) :
        NavigationPath(Routes.QUESTION_LIST.replace("{id}", id.toString()).replace("{parentId}", (parentId?.toString() ?: "-1")))

    object NavigateBack : NavigationPath("")
}


object Routes {
    const val MAIN_TOPIC_LIST = "topic"
    const val CHALLENGE = "challenge"
    const val TIMES = "times"
    const val HISTORY = "history"
    const val SETTINGS = "settings"

    const val SUB_TOPIC_LIST = "topic/{id}"
    const val QUESTION_LIST = "topic/{parentId}/questions/{id}"
    const val CHALLENGE_OPEN = "challenge/open/"
    const val CHALLENGE_SESSION = "challenge/{id}"
}


val NAVIGATION_BAR_DESTINATIONS = listOf(
    NavigationDestination.TopDestination(
        route = Routes.MAIN_TOPIC_LIST,
        selectedIcon = Icons.Rounded.Topic,
        unselectedIcon = Icons.Rounded.Topic,
        iconTextId = R.string.tab_topic
    ),
    NavigationDestination.TopDestination(
        route = Routes.CHALLENGE,
        selectedIcon = Icons.Rounded.Quiz,
        unselectedIcon = Icons.Rounded.Quiz,
        iconTextId = R.string.tab_challenge
    ),
    NavigationDestination.TopDestination(
        route = Routes.HISTORY,
        selectedIcon = Icons.Rounded.History,
        unselectedIcon = Icons.Rounded.History,
        iconTextId = R.string.tab_history
    ),
    NavigationDestination.TopDestination(
        route = Routes.TIMES,
        selectedIcon = Icons.Rounded.LockClock,
        unselectedIcon = Icons.Rounded.LockClock,
        iconTextId = R.string.tab_time
    ),
    NavigationDestination.TopDestination(
        route = Routes.SETTINGS,
        selectedIcon = Icons.Rounded.Settings,
        unselectedIcon = Icons.Rounded.Settings,
        iconTextId = R.string.tab_setting
    )
)