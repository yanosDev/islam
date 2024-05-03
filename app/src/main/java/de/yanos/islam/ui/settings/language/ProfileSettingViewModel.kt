package de.yanos.islam.ui.settings.language

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.islam.data.repositories.AuthRepository
import de.yanos.islam.util.settings.AppSettings
import de.yanos.islam.util.states.LoadState
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class ProfileSettingViewModel @Inject constructor(
    private val appSettings: AppSettings,
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentUser = authRepository.getAuthState(viewModelScope)
    var name by mutableStateOf(appSettings.userName)

    init {
        viewModelScope.launch {
            currentUser.collect {

            }
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            val result = authRepository.signInAnonymously()
            if (result is LoadState.Data && appSettings.userName.isBlank())
                updateUserName(result.data.user?.displayName ?: "")
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            updateUserName("")
        }
    }

    fun updateUserName(name: String) {
        appSettings.userName = name
        this.name = appSettings.userName
    }
}