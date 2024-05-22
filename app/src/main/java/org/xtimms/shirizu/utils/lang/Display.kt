package org.xtimms.shirizu.utils.lang

import android.content.res.Configuration

private const val TABLET_UI_REQUIRED_SCREEN_WIDTH_DP = 720

// some tablets have screen width like 711dp = 1600px / 2.25
private const val TABLET_UI_MIN_SCREEN_WIDTH_PORTRAIT_DP = 700

// make sure icons on the nav rail fit
private const val TABLET_UI_MIN_SCREEN_WIDTH_LANDSCAPE_DP = 600

fun Configuration.isTabletUi(): Boolean {
    return smallestScreenWidthDp >= TABLET_UI_REQUIRED_SCREEN_WIDTH_DP
}