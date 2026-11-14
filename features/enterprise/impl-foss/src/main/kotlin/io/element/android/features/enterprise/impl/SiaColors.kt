/*
 * SIA: "Code vert" theme (green on black, secret agent style).
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.features.enterprise.impl

import androidx.compose.ui.graphics.Color
import io.element.android.compound.colors.SemanticColorsLightDark
import io.element.android.compound.tokens.generated.SemanticColors
import io.element.android.compound.tokens.generated.compoundColorsDark

private val Black = Color(0xFF0A0F0C)
private val BlackLevel1 = Color(0xFF0D1510)
private val Card = Color(0xFF101A14)
private val CardStrong = Color(0xFF16241B)
private val DeepGreen = Color(0xFF0F3A24)
private val SubtleGreen = Color(0xFF0F2A1B)
private val Accent = Color(0xFF2FD27A)
private val AccentHovered = Color(0xFF29B96B)
private val AccentPressed = Color(0xFF239E5C)
private val AccentLight = Color(0xFF7CF0B0)
private val OnAccent = Color(0xFF04140A)
private val Text = Color(0xFFB8F5CF)
private val TextMuted = Color(0xFF6FA885)
private val TextFaint = Color(0xFF4E8A64)
private val Disabled = Color(0xFF35563F)
private val Line = Color(0xFF2A4534)
private val LineFaint = Color(0xFF1A2B20)

/** SIA palette: the dark Compound palette with every neutral and accent color turned to "Code vert". */
val siaCodeVertColors: SemanticColors = compoundColorsDark.copy(
    bgCanvasDefault = Black,
    bgCanvasDefaultLevel1 = BlackLevel1,
    bgCanvasDisabled = BlackLevel1,
    bgSubtlePrimary = CardStrong,
    bgSubtleSecondary = Card,
    bgSubtleSecondaryLevel0 = BlackLevel1,
    bgSubtleTertiary = BlackLevel1,
    bgActionPrimaryRest = Accent,
    bgActionPrimaryHovered = AccentHovered,
    bgActionPrimaryPressed = AccentPressed,
    bgActionPrimaryDisabled = LineFaint,
    bgActionSecondaryRest = Black,
    bgActionSecondaryHovered = CardStrong,
    bgActionSecondaryPressed = CardStrong,
    bgActionTertiaryRest = Black,
    bgActionTertiaryHovered = CardStrong,
    bgActionTertiarySelected = DeepGreen,
    bgAccentRest = Accent,
    bgAccentHovered = AccentHovered,
    bgAccentPressed = AccentPressed,
    bgAccentSelected = DeepGreen,
    bgAccentSubtle = SubtleGreen,
    bgBadgeAccent = DeepGreen,
    bgBadgeDefault = CardStrong,
    bgSuccessRest = Accent,
    bgSuccessHovered = AccentHovered,
    bgSuccessPressed = AccentPressed,
    bgSuccessSubtle = SubtleGreen,
    borderAccentPrimary = Accent,
    borderAccentSubtle = DeepGreen,
    borderDisabled = LineFaint,
    borderFocused = Accent,
    borderInteractiveHovered = TextMuted,
    borderInteractivePrimary = TextFaint,
    borderInteractiveSecondary = Line,
    borderSuccessPrimary = Accent,
    borderSuccessSubtle = DeepGreen,
    gradientActionStop1 = AccentLight,
    gradientActionStop2 = Accent,
    gradientActionStop3 = AccentHovered,
    gradientActionStop4 = AccentPressed,
    gradientSubtleStop1 = DeepGreen,
    gradientSubtleStop2 = SubtleGreen,
    gradientSubtleStop3 = Card,
    gradientSubtleStop4 = BlackLevel1,
    gradientSubtleStop5 = Black,
    gradientSubtleStop6 = Black,
    iconPrimary = Text,
    iconPrimaryAlpha = Text,
    iconSecondary = TextMuted,
    iconSecondaryAlpha = TextMuted,
    iconTertiary = TextFaint,
    iconTertiaryAlpha = TextFaint,
    iconQuaternary = Disabled,
    iconQuaternaryAlpha = Disabled,
    iconDisabled = Disabled,
    iconAccentPrimary = Accent,
    iconAccentTertiary = Accent,
    iconSuccessPrimary = Accent,
    iconOnSolidPrimary = OnAccent,
    separatorPrimary = Line,
    separatorSecondary = CardStrong,
    textPrimary = Text,
    textSecondary = TextMuted,
    textDisabled = Disabled,
    textActionPrimary = Text,
    textActionAccent = Accent,
    textActionSuccess = Accent,
    textSuccessPrimary = Accent,
    textBadgeAccent = AccentLight,
    textLinkExternal = AccentLight,
    textOnSolidPrimary = OnAccent,
)

/** The same palette is used whatever the phone setting: SIA is always dark. */
val siaColorsLightDark = SemanticColorsLightDark(light = siaCodeVertColors, dark = siaCodeVertColors)

