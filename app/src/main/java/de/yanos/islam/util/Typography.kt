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

fun typoByConfig(appSettings: AppSettings): Typography {
    return typo(
        sizeFactor = appSettings.fontSizeFactor,
        family = FontFamily(Font(FontStyle.values()[appSettings.fontStyle].fontId, FontWeight.Normal))
    )
}

val quranFont =  FontFamily(Font(R.font.quran_al_qalam, FontWeight.Normal))
enum class FontStyle(val textId: Int, val fontId: Int) {
    Alogical(R.string.font_alogical, R.font.alogical),
    OpenSans(R.string.font_open_sans, R.font.open_sans),
    Roboto(R.string.font_roboto, R.font.roboto),
    Ubuntu(R.string.font_ubuntu, R.font.ubuntu),
    Montserrat(R.string.font_montserrat, R.font.mont),
    AlQalam(R.string.font_qalam, R.font.quran_al_qalam)
}

fun typo(sizeFactor: Int, family: FontFamily): Typography {
    return Typography(
        displayLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Bold,
            fontSize = (56 + sizeFactor).sp,
            lineHeight = (64.0 + sizeFactor).sp,
            letterSpacing = (-0.025).sp,
            shadow = Shadow(
                offset = Offset(5f, 4f),
                blurRadius = 10f
            ),
        ),
        displayMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = (44 + sizeFactor).sp,
            lineHeight = (52.0 + sizeFactor).sp,
            letterSpacing = (-0.025).sp,
        ),
        displaySmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = (36 + sizeFactor).sp,
            lineHeight = (44.0 + sizeFactor).sp,
            letterSpacing = 0.0.sp,
        ),
        headlineLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Bold,
            fontSize = (32 + sizeFactor).sp,
            lineHeight = (40.0 + sizeFactor).sp,
            letterSpacing = 0.0.sp,
        ),
        headlineMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = (28 + sizeFactor).sp,
            lineHeight = (36.0 + sizeFactor).sp,
            letterSpacing = 0.0.sp,
        ),
        headlineSmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = (24 + sizeFactor).sp,
            lineHeight = (32.0 + sizeFactor).sp,
            letterSpacing = 0.0.sp,
        ),
        titleLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Bold,
            fontSize = (24 + sizeFactor).sp,
            lineHeight = (28.0 + sizeFactor).sp,
            letterSpacing = 0.0.sp,
        ),
        titleMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = (18 + sizeFactor).sp,
            lineHeight = (36.0 + sizeFactor).sp,
            letterSpacing = 0.015.sp,
        ),
        titleSmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = (14.0 + sizeFactor).sp,
            lineHeight = (20.0 + sizeFactor).sp,
            letterSpacing = 0.01.sp,
        ),
        bodyLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.SemiBold,
            fontSize = (18 + sizeFactor).sp,
            lineHeight = (26.0 + sizeFactor).sp,
            letterSpacing = 0.05.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = (16 + sizeFactor).sp,
            lineHeight = (22.0 + sizeFactor).sp,
            letterSpacing = 0.05.sp,
        ),
        bodySmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = (14 + sizeFactor).sp,
            lineHeight = (18.0 + sizeFactor).sp,
            letterSpacing = 0.05.sp,
        ),
        labelLarge = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.SemiBold,
            fontSize = (16 + sizeFactor).sp,
            lineHeight = (22.0 + sizeFactor).sp,
            letterSpacing = 0.05.sp,
        ),
        labelMedium = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Medium,
            fontSize = (16 + sizeFactor).sp,
            lineHeight = (18.0 + sizeFactor).sp,
            letterSpacing = 0.05.sp,
        ),
        labelSmall = TextStyle(
            fontFamily = family,
            fontWeight = FontWeight.Normal,
            fontSize = (13 + sizeFactor).sp,
            lineHeight = (18.0 + sizeFactor).sp,
            letterSpacing = 0.05.sp,
        ),
    )
}