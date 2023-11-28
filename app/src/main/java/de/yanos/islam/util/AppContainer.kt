package de.yanos.islam.util

import androidx.media3.session.MediaController
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppContainer @Inject constructor() {
    var audioController: MediaController? = null
}