package de.yanos.islam.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import retrofit2.Response
import timber.log.Timber
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


val goldColorDark = Color(android.graphics.Color.parseColor("#FFD700"))
val errorColorDark = Color(android.graphics.Color.parseColor("#FF0000"))
val correctColorDark = Color(android.graphics.Color.parseColor("#00FF00"))
val goldColorLight = goldColorDark.copy(alpha = 0.75f)
val errorColorLight = errorColorDark.copy(alpha = 0.75f)
val correctColorLight = correctColorDark.copy(alpha = 0.75f)


@Composable
fun typo() = MaterialTheme.typography

@Composable
fun labelSmall() = typo().labelSmall

@Composable
fun labelMedium() = typo().labelMedium

@Composable
fun labelLarge() = typo().labelLarge

@Composable
fun bodySmall() = typo().bodySmall

@Composable
fun bodyMedium() = typo().bodyMedium

@Composable
fun bodyLarge() = typo().bodyLarge

@Composable
fun titleSmall() = typo().titleSmall

@Composable
fun titleMedium() = typo().titleMedium

@Composable
fun titleLarge() = typo().titleLarge

@Composable
fun headlineSmall() = typo().headlineSmall

@Composable
fun headlineMedium() = typo().headlineMedium

@Composable
fun headlineLarge() = typo().headlineLarge

@Composable
fun goldColor(): Color {
    return if (isSystemInDarkTheme()) goldColorDark else goldColorLight
}

@Composable
fun correctColor(): Color {
    return if (isSystemInDarkTheme()) correctColorDark else correctColorLight
}

@Composable
fun errorColor(): Color {
    return if (isSystemInDarkTheme()) errorColorDark else errorColorLight
}

fun Long.epochSecondToDateString(
    format: String
): String {
    return try {
        val dueDate = Instant.ofEpochSecond(this).atZone(ZoneId.systemDefault()).toLocalDateTime()
        dueDate.format(DateTimeFormatter.ofPattern(format, Locale.getDefault()))
    } catch (e: Exception) {
        Timber.e(e)
        ""
    }
}

fun <T> localResponse(response: Response<T>): LoadState<T> {
    return if (response.isSuccessful) {
        response.body()?.let { body ->
            LoadState.Data(body)
        } ?: LoadState.Failure(Exception(response.errorBody().toString()))
    } else {
        LoadState.Failure(Exception(response.errorBody().toString()))
    }
}

fun <T> getData(response: LoadState<T>): T? {
    return (response as? LoadState.Data)?.let {
        it.data
    } ?: (response as? LoadState.Failure)?.let {
        Timber.e(it.e)
        null
    }
}