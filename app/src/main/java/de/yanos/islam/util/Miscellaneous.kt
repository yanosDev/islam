package de.yanos.islam.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


val goldColorDark = Color(android.graphics.Color.parseColor("#FFD700"))
val errorColorDark = Color(android.graphics.Color.parseColor("#FF0000"))
val correctColorDark = Color(android.graphics.Color.parseColor("#00FF00"))
val goldColorLight = goldColorDark.copy(alpha = 0.75f)
val errorColorLight = errorColorDark.copy(alpha = 0.75f)
val correctColorLight = correctColorDark.copy(alpha = 0.75f)

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