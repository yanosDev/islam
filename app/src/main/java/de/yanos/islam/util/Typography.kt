package de.yanos.islam.util

import androidx.compose.material3.Typography
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import de.yanos.islam.R

val Sabana = FontFamily(
    Font(R.font.sabana, FontWeight.Normal)
)

val Sirajun = FontFamily(
    Font(R.font.sirajun, FontWeight.Medium)
)
val Mustopha = FontFamily(
    Font(R.font.mustopha_regular, FontWeight.Medium)
)

val Khodijah = FontFamily(
    Font(R.font.khodijah, FontWeight.Medium)
)

val SabanaTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Sabana,
        fontWeight = FontWeight.Normal,
        fontSize = 60.sp,
        lineHeight = 68.0.sp,
        letterSpacing = (-0.025).sp,
        shadow = Shadow(
            offset = Offset(5f, 4f),
            blurRadius = 10f
        ),
    ),
    displayMedium = TextStyle(
        fontFamily = Sabana,
        fontWeight = FontWeight.Normal,
        fontSize = 48.sp,
        lineHeight = 56.0.sp,
        letterSpacing = (-0.025).sp,
    ),
    displaySmall = TextStyle(
        fontFamily = Sabana,
        fontWeight = FontWeight.Normal,
        fontSize = 40.sp,
        lineHeight = 48.0.sp,
        letterSpacing = 0.0.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = Sabana,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.0.sp,
        letterSpacing = 0.0.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = Sabana,
        fontWeight = FontWeight.Medium,
        fontSize = 32.sp,
        lineHeight = 40.0.sp,
        letterSpacing = 0.0.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = Sabana,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.0.sp,
        letterSpacing = 0.0.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = Sabana,
        fontWeight = FontWeight.Normal,
        fontSize = 26.sp,
        lineHeight = 32.0.sp,
        letterSpacing = 0.0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = Sabana,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 40.0.sp,
        letterSpacing = 0.015.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = Sabana,
        fontWeight = FontWeight.Medium,
        fontSize = 18.0.sp,
        lineHeight = 24.0.sp,
        letterSpacing = 0.01.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = Sabana,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 30.0.sp,
        letterSpacing = 0.05.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = Sabana,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.0.sp,
        letterSpacing = 0.05.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = Sabana,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 22.0.sp,
        letterSpacing = 0.05.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = Sabana,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 26.0.sp,
        letterSpacing = 0.05.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = Sabana,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 22.0.sp,
        letterSpacing = 0.05.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = Sabana,
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp,
        lineHeight = 22.0.sp,
        letterSpacing = 0.05.sp,
    ),
)
val SirajunTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Sirajun,
        fontWeight = FontWeight.Normal,
        fontSize = 56.sp,
        lineHeight = 64.0.sp,
        letterSpacing = (-0.025).sp,
        shadow = Shadow(
            offset = Offset(5f, 4f),
            blurRadius = 10f
        ),
    ),
    displayMedium = TextStyle(
        fontFamily = Sirajun,
        fontWeight = FontWeight.Normal,
        fontSize = 44.sp,
        lineHeight = 52.0.sp,
        letterSpacing = (-0.025).sp,
    ),
    displaySmall = TextStyle(
        fontFamily = Sirajun,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.0.sp,
        letterSpacing = 0.0.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = Sirajun,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.0.sp,
        letterSpacing = 0.0.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = Sirajun,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
        lineHeight = 36.0.sp,
        letterSpacing = 0.0.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = Sirajun,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.0.sp,
        letterSpacing = 0.0.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = Sirajun,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.0.sp,
        letterSpacing = 0.0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = Sirajun,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 36.0.sp,
        letterSpacing = 0.015.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = Sirajun,
        fontWeight = FontWeight.Medium,
        fontSize = 14.0.sp,
        lineHeight = 20.0.sp,
        letterSpacing = 0.01.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = Sirajun,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.0.sp,
        letterSpacing = 0.05.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = Sirajun,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.0.sp,
        letterSpacing = 0.05.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = Sirajun,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.0.sp,
        letterSpacing = 0.05.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = Sirajun,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.0.sp,
        letterSpacing = 0.05.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = Sirajun,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 16.0.sp,
        letterSpacing = 0.05.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = Sirajun,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.0.sp,
        letterSpacing = 0.05.sp,
    ),
)

val MustophaTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Mustopha,
        fontWeight = FontWeight.Normal,
        fontSize = 56.sp,
        lineHeight = 64.0.sp,
        letterSpacing = (-0.025).sp,
        shadow = Shadow(
            offset = Offset(5f, 4f),
            blurRadius = 10f
        ),
    ),
    displayMedium = TextStyle(
        fontFamily = Mustopha,
        fontWeight = FontWeight.Normal,
        fontSize = 44.sp,
        lineHeight = 52.0.sp,
        letterSpacing = (-0.025).sp,
    ),
    displaySmall = TextStyle(
        fontFamily = Mustopha,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.0.sp,
        letterSpacing = 0.0.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = Mustopha,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.0.sp,
        letterSpacing = 0.0.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = Mustopha,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
        lineHeight = 36.0.sp,
        letterSpacing = 0.0.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = Mustopha,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.0.sp,
        letterSpacing = 0.0.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = Mustopha,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.0.sp,
        letterSpacing = 0.0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = Mustopha,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 36.0.sp,
        letterSpacing = 0.015.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = Mustopha,
        fontWeight = FontWeight.Medium,
        fontSize = 14.0.sp,
        lineHeight = 20.0.sp,
        letterSpacing = 0.01.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = Mustopha,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.0.sp,
        letterSpacing = 0.05.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = Mustopha,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.0.sp,
        letterSpacing = 0.05.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = Mustopha,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.0.sp,
        letterSpacing = 0.05.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = Mustopha,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.0.sp,
        letterSpacing = 0.05.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = Mustopha,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 16.0.sp,
        letterSpacing = 0.05.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = Mustopha,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.0.sp,
        letterSpacing = 0.05.sp,
    ),
)


val KhodjahTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Khodijah,
        fontWeight = FontWeight.Normal,
        fontSize = 56.sp,
        lineHeight = 64.0.sp,
        letterSpacing = (-0.025).sp,
        shadow = Shadow(
            offset = Offset(5f, 4f),
            blurRadius = 10f
        ),
    ),
    displayMedium = TextStyle(
        fontFamily = Khodijah,
        fontWeight = FontWeight.Normal,
        fontSize = 44.sp,
        lineHeight = 52.0.sp,
        letterSpacing = (-0.025).sp,
    ),
    displaySmall = TextStyle(
        fontFamily = Khodijah,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.0.sp,
        letterSpacing = 0.0.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = Khodijah,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.0.sp,
        letterSpacing = 0.0.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = Khodijah,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
        lineHeight = 36.0.sp,
        letterSpacing = 0.0.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = Khodijah,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.0.sp,
        letterSpacing = 0.0.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = Khodijah,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.0.sp,
        letterSpacing = 0.0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = Khodijah,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 36.0.sp,
        letterSpacing = 0.015.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = Khodijah,
        fontWeight = FontWeight.Medium,
        fontSize = 14.0.sp,
        lineHeight = 20.0.sp,
        letterSpacing = 0.01.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = Khodijah,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.0.sp,
        letterSpacing = 0.05.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = Khodijah,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.0.sp,
        letterSpacing = 0.05.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = Khodijah,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.0.sp,
        letterSpacing = 0.05.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = Khodijah,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.0.sp,
        letterSpacing = 0.05.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = Khodijah,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 16.0.sp,
        letterSpacing = 0.05.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = Khodijah,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.0.sp,
        letterSpacing = 0.05.sp,
    ),
)