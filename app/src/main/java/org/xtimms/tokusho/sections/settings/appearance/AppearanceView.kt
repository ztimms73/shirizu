package org.xtimms.tokusho.sections.settings.appearance

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.material.icons.outlined.Timelapse
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.android.material.color.DynamicColors
import org.xtimms.tokusho.LocalDarkTheme
import org.xtimms.tokusho.LocalDynamicColorSwitch
import org.xtimms.tokusho.LocalPaletteStyleIndex
import org.xtimms.tokusho.LocalSeedColor
import org.xtimms.tokusho.R
import org.xtimms.tokusho.core.components.PreferenceItem
import org.xtimms.tokusho.core.components.PreferenceSubtitle
import org.xtimms.tokusho.core.components.PreferenceSwitch
import org.xtimms.tokusho.core.components.PreferenceSwitchWithDivider
import org.xtimms.tokusho.core.components.ScaffoldWithTopAppBar
import org.xtimms.tokusho.core.prefs.AppSettings
import org.xtimms.tokusho.core.prefs.DarkThemePreference.Companion.OFF
import org.xtimms.tokusho.core.prefs.DarkThemePreference.Companion.ON
import org.xtimms.tokusho.core.prefs.READING_TIME
import org.xtimms.tokusho.core.prefs.STYLE_MONOCHROME
import org.xtimms.tokusho.core.prefs.STYLE_TONAL_SPOT
import org.xtimms.tokusho.core.prefs.TABS_MANGA_COUNT
import org.xtimms.tokusho.core.prefs.paletteStyles
import org.xtimms.tokusho.ui.harmonize.hct.Hct
import org.xtimms.tokusho.ui.monet.LocalTonalPalettes
import org.xtimms.tokusho.ui.monet.PaletteStyle
import org.xtimms.tokusho.ui.monet.TonalPalettes
import org.xtimms.tokusho.ui.monet.TonalPalettes.Companion.toTonalPalettes
import org.xtimms.tokusho.ui.monet.a1
import org.xtimms.tokusho.ui.monet.a2
import org.xtimms.tokusho.ui.monet.a3
import org.xtimms.tokusho.utils.system.getLanguageDesc

const val APPEARANCE_DESTINATION = "appearance"

val colorList = ((4..10) + (1..3)).map { it * 35.0 }.map { Color(Hct.from(it, 40.0, 40.0).toInt()) }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppearanceView(
    navigateBack: () -> Unit,
    navigateToDarkTheme: () -> Unit,
    navigateToLanguages: () -> Unit
) {
    val image by remember {
        mutableIntStateOf(
            listOf(
                R.drawable.ookami, R.drawable.sample1
            ).random()
        )
    }

    var isReadingTimeEstimationEnabled by remember {
        mutableStateOf(AppSettings.isReadingTimeEstimationEnabled())
    }

    ScaffoldWithTopAppBar(
        title = stringResource(R.string.appearance),
        navigateBack = navigateBack
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            MangaCard(
                modifier = Modifier.padding(18.dp),
                thumbnailUrl = image
            )

            val pageCount = colorList.size + 1
            val pagerState = rememberPagerState(initialPage = if (LocalPaletteStyleIndex.current == STYLE_MONOCHROME) pageCount else colorList.indexOf(
                Color(LocalSeedColor.current)
            ).run { if (this == -1) 0 else this }) {
                pageCount
            }

            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth()
                    .clearAndSetSemantics { },
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) { page ->
                if (page < pageCount - 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) { ColorButtons(colorList[page]) }
                } else {
                    val isSelected =
                        LocalPaletteStyleIndex.current == STYLE_MONOCHROME && !LocalDynamicColorSwitch.current
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        ColorButtonImpl(
                            modifier = Modifier,
                            isSelected = { isSelected },
                            tonalPalettes = Color.Black.toTonalPalettes(PaletteStyle.Monochrome),
                            onClick = {
                                AppSettings.switchDynamicColor(enabled = false)
                                AppSettings.modifyThemeSeedColor(
                                    Color.Black.toArgb(), STYLE_MONOCHROME
                                )
                            })
                    }
                }
            }

            HorizontalPagerIndicator(pagerState = pagerState,
                pageCount = pageCount,
                modifier = Modifier
                    .clearAndSetSemantics { }
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 12.dp),
                activeColor = MaterialTheme.colorScheme.primary,
                inactiveColor = MaterialTheme.colorScheme.outlineVariant,
                indicatorHeight = 6.dp,
                indicatorWidth = 6.dp)

            if (DynamicColors.isDynamicColorAvailable()) {
                PreferenceSwitch(
                    title = stringResource(id = R.string.dynamic_color),
                    description = stringResource(id = R.string.dynamic_color_desc),
                    icon = Icons.Outlined.ColorLens,
                    isChecked = LocalDynamicColorSwitch.current,
                    onClick = {
                        AppSettings.switchDynamicColor()
                    })
            }
            val isDarkTheme = LocalDarkTheme.current.isDarkTheme()
            PreferenceSwitchWithDivider(
                title = stringResource(id = R.string.dark_theme),
                icon = if (isDarkTheme) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                isChecked = isDarkTheme,
                description = LocalDarkTheme.current.getDarkThemeDesc(),
                onChecked = { AppSettings.modifyDarkThemePreference(if (isDarkTheme) OFF else ON) },
                onClick = { navigateToDarkTheme() })
            PreferenceItem(
                title = stringResource(id = R.string.language),
                icon = Icons.Outlined.Language,
                description = getLanguageDesc(),
                onClick = { navigateToLanguages() })
            PreferenceSubtitle(text = stringResource(id = R.string.details))
            PreferenceSwitch(
                title = stringResource(id = R.string.show_estimated_read_time),
                description = stringResource(id = R.string.show_estimated_read_time_desc),
                icon = Icons.Outlined.Timelapse,
                isChecked = isReadingTimeEstimationEnabled,
                onClick = {
                    isReadingTimeEstimationEnabled = !isReadingTimeEstimationEnabled
                    AppSettings.updateValue(READING_TIME, isReadingTimeEstimationEnabled)
                })
        }
    }
}

@Composable
fun RowScope.ColorButtons(color: Color) {
    paletteStyles.subList(STYLE_TONAL_SPOT, STYLE_MONOCHROME).forEachIndexed { index, style ->
        ColorButton(color = color, index = index, tonalStyle = style)
    }
}

@Composable
fun RowScope.ColorButton(
    modifier: Modifier = Modifier,
    color: Color = Color.Green,
    index: Int = 0,
    tonalStyle: PaletteStyle = PaletteStyle.TonalSpot,
) {
    val tonalPalettes by remember {
        mutableStateOf(color.toTonalPalettes(tonalStyle))
    }
    val isSelect =
        !LocalDynamicColorSwitch.current && LocalSeedColor.current == color.toArgb() && LocalPaletteStyleIndex.current == index
    ColorButtonImpl(modifier = modifier, tonalPalettes = tonalPalettes, isSelected = { isSelect }) {
        AppSettings.switchDynamicColor(enabled = false)
        AppSettings.modifyThemeSeedColor(color.toArgb(), index)
    }

}

@Composable
fun RowScope.ColorButtonImpl(
    modifier: Modifier = Modifier,
    isSelected: () -> Boolean = { false },
    tonalPalettes: TonalPalettes,
    cardColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    onClick: () -> Unit = {}
) {

    val containerSize by animateDpAsState(targetValue = if (isSelected.invoke()) 28.dp else 0.dp)
    val iconSize by animateDpAsState(targetValue = if (isSelected.invoke()) 16.dp else 0.dp)

    Surface(
        modifier = modifier
            .padding(4.dp)
            .sizeIn(maxHeight = 80.dp, maxWidth = 80.dp, minHeight = 64.dp, minWidth = 64.dp)
            .weight(1f, false)
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        color = cardColor,
        onClick = onClick
    ) {
        CompositionLocalProvider(LocalTonalPalettes provides tonalPalettes) {
            val color1 = 80.a1
            val color2 = 90.a2
            val color3 = 60.a3
            Box(Modifier.fillMaxSize()) {
                Box(modifier = modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .drawBehind { drawCircle(color1) }
                    .align(Alignment.Center)) {
                    Surface(
                        color = color2, modifier = Modifier
                            .align(Alignment.BottomStart)
                            .size(24.dp)
                    ) {}
                    Surface(
                        color = color3, modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(24.dp)
                    ) {}
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clip(CircleShape)
                            .size(containerSize)
                            .drawBehind { drawCircle(containerColor) },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                            modifier = Modifier
                                .size(iconSize)
                                .align(Alignment.Center),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                }
            }
        }
    }
}