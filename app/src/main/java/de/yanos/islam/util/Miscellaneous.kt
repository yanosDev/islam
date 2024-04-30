package de.yanos.islam.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.TextUnit
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.gms.location.LocationServices
import de.yanos.core.utils.findActivity
import retrofit2.Response
import timber.log.Timber
import java.io.File
import java.text.CharacterIterator
import java.text.StringCharacterIterator
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

val selectedTextColor = Color(android.graphics.Color.parseColor("#6AB189"))
val quranInnerColor = Color(android.graphics.Color.parseColor("#DED6CD"))
val goldColorDark = Color(android.graphics.Color.parseColor("#FFD700"))
val errorColorDark = Color(android.graphics.Color.parseColor("#FF0000"))
val correctColorDark = Color(android.graphics.Color.parseColor("#00FF00"))
val goldColorLight = Color(android.graphics.Color.parseColor("#EEBC1D"))
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
        try {
            LoadState.Failure(Exception(response.errorBody().toString()), response.code())
        } catch (e: Exception) {
            Timber.e(e)
            LoadState.Failure(e)
        }
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

@Composable
fun getAnnotatedString(query: String, name: String, highlightStyle: SpanStyle): AnnotatedString {
    //Find where searchQuery appears in courseName
    var startIndex = 0
    val builder = AnnotatedString.Builder(name)
    if (query.isNotBlank())
        while (startIndex >= 0) {
            startIndex = name.indexOf(query, startIndex, true)
            //If the query is in the name, add a style, otherwise do nothing
            if (startIndex >= 0) {
                val endIndex = startIndex + query.length
                builder.addStyle(highlightStyle, startIndex, endIndex)
                startIndex = endIndex
            }
        }
    return builder.toAnnotatedString()
}

@Composable
fun alternatingColors(
    primaryColor: Color = goldColor(),
    text: String,
    delimiter: Regex = Regex(" ")
): AnnotatedString {
    //Find where searchQuery appears in courseName
    val primaryStyle = SpanStyle(color = primaryColor)
    val builder = AnnotatedString.Builder(text)
    var startIndex = 0
    text.split(delimiter).takeIf { it.isNotEmpty() }?.forEachIndexed { index, part ->
        val newIndex = startIndex + part.length + 1
        if (index % 2 == 0)
            builder.addStyle(primaryStyle, startIndex, startIndex + part.length)
        startIndex = newIndex
    } ?: run {
        builder.addStyle(primaryStyle, 0, text.length)
    }
    return builder.toAnnotatedString()
}

@Composable
fun ayahWithColoredNumber(
    primaryColor: Color = goldColor(),
    text: String,
    ayahNr: Int,
    fontSize: TextUnit,
    isSelected: Boolean
): AnnotatedString {
    //Find where searchQuery appears in courseName
    val primaryStyle = SpanStyle(color = primaryColor, fontSize = fontSize.times(0.6))
    val secondaryStyle = if (isSelected)
        SpanStyle(
            color = selectedTextColor,
            shadow = Shadow(offset = Offset(2F, 1F)),
        )
    else SpanStyle(color = MaterialTheme.colorScheme.onSurface)
    val ayahEnd = " \uFD3F" + arabicNumber(ayahNr) + "\uFD3E "
    val builder = AnnotatedString.Builder(text + ayahEnd)
    builder.addStyle(secondaryStyle, 0, text.length)
    builder.addStyle(primaryStyle, text.length, text.length + ayahEnd.length)
    return builder.toAnnotatedString()
}

fun hasLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}


fun hasNotificationPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}


@SuppressLint("MissingPermission")
fun getCurrentLocation(context: Context, callback: (Double, Double) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location ->
            if (location != null) {
                val lat = location.latitude
                val long = location.longitude
                callback(lat, long)
            }
        }
        .addOnFailureListener { exception ->
            // Handle location retrieval failure
            exception.printStackTrace()
        }
}

fun arabicNumber(number: Int): String = number.toString().map { character ->
    when (character.digitToInt()) {
        0 -> "\u0660"
        1 -> "\u0661"
        2 -> "\u0662"
        3 -> "\u0663"
        4 -> "\u0664"
        5 -> "\u0665"
        6 -> "\u0666"
        7 -> "\u0667"
        8 -> "\u0668"
        else -> "\u0669"
    }
}.joinToString("")

fun String.localFile(dir: File) = File(dir, Uri.parse(this).path!!)

fun Long.humanReadableByteCountSI(): String {
    var bytes = this
    if (-1000 < bytes && bytes < 1000) {
        return "$bytes B"
    }
    val ci: CharacterIterator = StringCharacterIterator("kMGTPE")
    while (bytes <= -999950 || bytes >= 999950) {
        bytes /= 1000
        ci.next()
    }
    return String.format("%.1f %cB", bytes / 1000.0, ci.current())
}

fun Context.setScreenOrientation(orientation: Int) {
    val activity = this.findActivity()
    activity.requestedOrientation = orientation
    if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
        hideSystemUi()
    } else {
        showSystemUi()
    }
}

fun Context.hideSystemUi() {
    val activity = this.findActivity()
    val window = activity.window ?: return
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowInsetsControllerCompat(window, window.decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

fun Context.showSystemUi() {
    val activity = this.findActivity()
    val window = activity.window ?: return
    WindowCompat.setDecorFitsSystemWindows(window, true)
    WindowInsetsControllerCompat(
        window,
        window.decorView
    ).show(WindowInsetsCompat.Type.systemBars())
}

inline fun <T1 : Any, T2 : Any, R : Any> safeLet(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
    return if (p1 != null && p2 != null) block(p1, p2) else null
}

inline fun <T1 : Any, T2 : Any, T3 : Any, R : Any> safeLet(p1: T1?, p2: T2?, p3: T3?, block: (T1, T2, T3) -> R?): R? {
    return if (p1 != null && p2 != null && p3 != null) block(p1, p2, p3) else null
}