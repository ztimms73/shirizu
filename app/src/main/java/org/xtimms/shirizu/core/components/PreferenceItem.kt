package org.xtimms.shirizu.core.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.material.icons.outlined.ToggleOn
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.xtimms.shirizu.ui.theme.FixedAccentColors
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.ShirizuAsyncImage
import org.xtimms.shirizu.ui.monet.LocalTonalPalettes
import org.xtimms.shirizu.ui.monet.TonalPalettes.Companion.toTonalPalettes
import org.xtimms.shirizu.ui.theme.PreviewThemeLight
import org.xtimms.shirizu.ui.theme.ShirizuTheme
import org.xtimms.shirizu.ui.theme.applyOpacity
import org.xtimms.shirizu.utils.FileSize

private const val horizontal = 8
private const val vertical = 16

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PreferenceItem(
    title: String,
    description: String? = null,
    icon: Any? = null,
    enabled: Boolean = true,
    onLongClickLabel: String? = null,
    onLongClick: (() -> Unit)? = null,
    onClickLabel: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit = {},
) {
    Surface(
        modifier = Modifier.combinedClickable(
            onClick = onClick,
            onClickLabel = onClickLabel,
            enabled = enabled,
            onLongClickLabel = onLongClickLabel,
            onLongClick = onLongClick
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal.dp, vertical.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leadingIcon?.invoke()

            when (icon) {
                is ImageVector -> {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 8.dp, end = 16.dp)
                            .size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.applyOpacity(enabled)
                    )
                }

                is Painter -> {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 8.dp, end = 16.dp)
                            .size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.applyOpacity(enabled)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .then(
                        if (icon != null)
                            Modifier
                                .padding(horizontal = 16.dp)
                                .padding(end = 8.dp)
                        else Modifier.padding(horizontal = 8.dp)
                    )
            ) {
                PreferenceItemTitle(text = title, enabled = enabled)
                if (!description.isNullOrEmpty()) PreferenceItemDescription(
                    text = description,
                    enabled = enabled
                )
            }
            trailingIcon?.let {
                VerticalDivider(
                    modifier = Modifier
                        .height(32.dp)
                        .padding(horizontal = 8.dp)
                        .align(Alignment.CenterVertically),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    thickness = 1.dp
                )
                trailingIcon.invoke()
            }
        }
    }
}

@Composable
internal fun PreferenceItemTitle(
    modifier: Modifier = Modifier,
    text: String,
    maxLines: Int = 2,
    enabled: Boolean,
    color: Color = MaterialTheme.colorScheme.onBackground,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    Text(
        modifier = modifier,
        text = text,
        maxLines = maxLines,
        color = color.applyOpacity(enabled),
        overflow = overflow
    )
}

@Composable
internal fun PreferenceItemDescription(
    modifier: Modifier = Modifier,
    text: String,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    enabled: Boolean,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    AnimatedContent(
        targetState = text,
        transitionSpec = {
            if (targetState > initialState) {
                (slideInVertically { height -> -height } + fadeIn()).togetherWith(
                    slideOutVertically { height -> height } + fadeOut())
            } else {
                (slideInVertically { height -> height } + fadeIn()).togetherWith(
                    slideOutVertically { height -> -height } + fadeOut())
            }.using(SizeTransform(clip = false))
        },
        modifier = modifier.padding(top = 2.dp),
        label = "Preference desc"
    ) { targetText ->
        Text(
            text = targetText,
            maxLines = maxLines,
            style = style,
            color = color.applyOpacity(enabled),
            overflow = overflow
        )
    }
}

@Composable
fun PreferenceSwitchWithDivider(
    title: String,
    description: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isSwitchEnabled: Boolean = enabled,
    isChecked: Boolean = true,
    checkedIcon: ImageVector = Icons.Outlined.Check,
    onClick: (() -> Unit) = {},
    onChecked: () -> Unit = {}
) {
    val thumbContent: (@Composable () -> Unit)? = if (isChecked) {
        {
            Icon(
                imageVector = checkedIcon,
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize),
            )
        }
    } else {
        null
    }
    Surface(
        modifier = Modifier.clickable(
            enabled = enabled,
            onClick = onClick,
            onClickLabel = stringResource(id = R.string.open_settings)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal.dp, vertical.dp)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.let {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 16.dp)
                        .size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.applyOpacity(enabled)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                PreferenceItemTitle(text = title, enabled = enabled)
                if (!description.isNullOrEmpty()) PreferenceItemDescription(
                    text = description,
                    enabled = enabled
                )
            }
            VerticalDivider(
                modifier = Modifier
                    .height(32.dp)
                    .padding(horizontal = 8.dp)
                    .width(1f.dp)
                    .align(Alignment.CenterVertically),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
            Switch(
                checked = isChecked,
                onCheckedChange = { onChecked() },
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .semantics {
                        contentDescription = title
                    },
                enabled = isSwitchEnabled,
                thumbContent = thumbContent
            )
        }
    }
}

@Composable
fun PreferenceSwitchWithContainer(
    title: String,
    icon: ImageVector? = null,
    isChecked: Boolean,
    onClick: () -> Unit,
) {
    val thumbContent: (@Composable () -> Unit)? = if (isChecked) {
        {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize),
            )
        }
    } else {
        null
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(
                if (isChecked) FixedAccentColors.primaryFixed else MaterialTheme.colorScheme.outline
            )
            .toggleable(value = isChecked) { onClick() }
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon?.let {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 8.dp, end = 16.dp)
                    .size(24.dp),
                tint = if (isChecked) FixedAccentColors.onPrimaryFixed else MaterialTheme.colorScheme.surface
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = if (icon == null) 12.dp else 0.dp, end = 12.dp)
        ) {
            with(MaterialTheme) {
                Text(
                    text = title,
                    maxLines = 2,
                    color = if (isChecked) FixedAccentColors.onPrimaryFixed else colorScheme.surface
                )
            }
        }
        Switch(
            checked = isChecked,
            onCheckedChange = null,
            modifier = Modifier.padding(start = 12.dp, end = 6.dp),
            thumbContent = thumbContent,
            colors = SwitchDefaults.colors(
                checkedIconColor = FixedAccentColors.onPrimaryFixed,
                checkedThumbColor = FixedAccentColors.primaryFixed,
                checkedTrackColor = FixedAccentColors.onPrimaryFixedVariant,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
fun PreferenceSubtitle(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(start = 18.dp, top = 24.dp, bottom = 12.dp),
    text: String,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding),
        color = color,
        style = MaterialTheme.typography.labelLarge
    )
}

@Composable
fun PreferenceSingleChoiceItem(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    contentPadding: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 18.dp),
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.selectable(
            selected = selected, onClick = onClick
        )
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp)
            ) {
                Text(
                    text = text,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                    color = MaterialTheme.colorScheme.onSurface,
                    overflow = TextOverflow.Ellipsis
                )
            }
            RadioButton(
                selected = selected,
                onClick = onClick,
                modifier = Modifier
                    .padding()
                    .clearAndSetSemantics { },
            )
        }
    }
}

@Composable
fun PreferenceInfo(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector = Icons.Outlined.Info,
    applyPaddings: Boolean = true
) {
    Row(modifier = modifier
        .fillMaxWidth()
        .run {
            if (applyPaddings) padding(horizontal = 16.dp, vertical = 16.dp)
            else this
        }) {
        Icon(
            modifier = Modifier.padding(), imageVector = icon, contentDescription = null
        )
        Text(
            modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.CenterVertically),
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun PreferenceSwitch(
    title: String,
    description: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isChecked: Boolean = true,
    checkedIcon: ImageVector = Icons.Outlined.Check,
    onClick: (() -> Unit) = {},
) {
    val thumbContent: (@Composable () -> Unit)? = if (isChecked) {
        {
            Icon(
                imageVector = checkedIcon,
                contentDescription = null,
                modifier = Modifier.size(SwitchDefaults.IconSize),
            )
        }
    } else {
        null
    }
    Surface(
        modifier = Modifier.toggleable(value = isChecked,
            enabled = enabled,
            onValueChange = { onClick() })
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal.dp, vertical.dp)
                .padding(start = if (icon == null) 12.dp else 0.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.let {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 16.dp)
                        .size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.applyOpacity(enabled)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                PreferenceItemTitle(
                    text = title, enabled = enabled
                )
                if (!description.isNullOrEmpty()) PreferenceItemDescription(
                    text = description, enabled = enabled
                )
            }
            Switch(
                checked = isChecked,
                onCheckedChange = null,
                modifier = Modifier.padding(start = 20.dp, end = 6.dp),
                enabled = enabled,
                thumbContent = thumbContent
            )
        }
    }
}

@Composable
fun PreferencesHintCard(
    title: String = "Title ".repeat(2),
    description: String? = "Description text ".repeat(3),
    icon: ImageVector? = Icons.Outlined.Translate,
    containerColor: Color = FixedAccentColors.secondaryFixed,
    contentColor: Color = FixedAccentColors.onSecondaryFixed,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(containerColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon?.let {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 8.dp, end = 24.dp)
                    .size(24.dp),
                tint = contentColor
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = if (icon == null) 12.dp else 0.dp, end = 12.dp)
        ) {
            with(MaterialTheme) {
                Text(
                    text = title,
                    maxLines = 1,
                    style = typography.titleLarge.copy(fontSize = 20.sp),
                    color = contentColor
                )
                if (description != null) Text(
                    text = description,
                    color = contentColor,
                    maxLines = 2, overflow = TextOverflow.Ellipsis,
                    style = typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
fun PreferenceStorageHeader(
    used: Float = 40F,
    total: Float = 128F
) {

    val animatedProgress = animateFloatAsState(
        targetValue = 1 - ((total - used) / total),
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "Progress"
    ).value

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedNumber(
                value = FileSize.BYTES.formatWithoutUnits(used)
            )
            AnimatedContent(
                targetState = used,
                transitionSpec = {
                    if (targetState > initialState) {
                        (fadeIn()).togetherWith(fadeOut())
                    } else {
                        (fadeIn()).togetherWith(fadeOut())
                    }.using(SizeTransform(clip = false))
                },
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.Bottom)
                    .padding(PaddingValues(start = 4.dp, bottom = 12.dp)),
                label = "Unit"
            ) { targetUsed ->
                Text(text = FileSize.BYTES.showUnit(LocalContext.current, targetUsed))
            }
            AnimatedContent(
                targetState = total,
                transitionSpec = {
                    if (targetState > initialState) {
                        (fadeIn()).togetherWith(fadeOut())
                    } else {
                        (fadeIn()).togetherWith(fadeOut())
                    }.using(SizeTransform(clip = false))
                },
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .padding(PaddingValues(bottom = 12.dp)),
                label = "Total used"
            ) { targetTotal ->
                Text(
                    text = FileSize.BYTES.totalFormat(LocalContext.current, targetTotal),
                )
            }
        }
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .padding(PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp))
                .height(16.dp)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primaryContainer,
            strokeCap = StrokeCap.Round,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PreferenceStorageItem(
    title: String,
    used: Float = 0F,
    total: Float = 0F,
    icon: Any? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit = {},
) {

    val animatedProgress = animateFloatAsState(
        targetValue = 1 - ((total - used) / total),
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "Progress"
    ).value

    Surface(
        modifier = Modifier.combinedClickable(
            onClick = onClick,
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal.dp, vertical.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leadingIcon?.invoke()

            when (icon) {
                is ImageVector -> {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 8.dp, end = 16.dp)
                            .size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.applyOpacity(true),
                    )
                }

                is Painter -> {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 8.dp, end = 16.dp)
                            .size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.applyOpacity(true),
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .then(
                        if (icon != null)
                            Modifier
                                .padding(start = 16.dp, end = 8.dp)
                        else Modifier.padding(horizontal = 8.dp)
                    )
            ) {
                Row {
                    PreferenceItemTitle(
                        modifier = Modifier.weight(1f),
                        text = title,
                        enabled = true
                    )
                    AnimatedContent(
                        targetState = used,
                        transitionSpec = {
                            if (targetState > initialState) {
                                (fadeIn()).togetherWith(fadeOut())
                            } else {
                                (fadeIn()).togetherWith(fadeOut())
                            }.using(SizeTransform(clip = false))
                        },
                        label = "Total used"
                    ) { targetTotal ->
                        Text(text = FileSize.BYTES.format(LocalContext.current, targetTotal))
                    }
                }
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .padding(PaddingValues(top = 12.dp))
                        .height(5.dp)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer,
                    strokeCap = StrokeCap.Round,
                )
            }
            trailingIcon?.let {
                VerticalDivider(
                    modifier = Modifier
                        .height(32.dp)
                        .padding(horizontal = 8.dp)
                        .align(Alignment.CenterVertically),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    thickness = 1.dp
                )
                trailingIcon.invoke()
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreferenceStorageHeaderPreview() {
    ShirizuTheme {
        PreferenceStorageHeader()
    }
}

@Composable
@Preview(showBackground = true)
fun PreferenceStorageItemPreview() {
    ShirizuTheme {
        PreferenceStorageItem(title = "Saved manga", icon = Icons.Outlined.Save, total = 0F)
    }
}

@Composable
@Preview
fun PreferenceItemPreview() {
    Column {
        PreferenceItem(title = "title", description = "description")
        PreferenceItem(title = "title", description = "description", icon = Icons.Outlined.Update)
    }
}

@Composable
@Preview
fun PreferenceSwitchPreview() {
    PreferenceSwitch(
        title = "PreferenceSwitch",
        description = "Supporting text",
        icon = Icons.Outlined.ToggleOn,
    )
}

@Composable
@Preview
fun PreferenceSwitchWithDividerPreview() {
    PreferenceSwitchWithDivider(
        title = "PreferenceSwitch",
        description = "Supporting text",
        icon = Icons.Outlined.Call,
    )
}

@Composable
@Preview
private fun PreferenceSwitchWithContainerPreview() {
    var isChecked by remember { mutableStateOf(true) }
    PreviewThemeLight {
        PreferenceSwitchWithContainer(
            title = "Title ".repeat(2),
            isChecked = isChecked,
            onClick = { isChecked = !isChecked },
            icon = null
        )
    }
}

@Composable
@Preview(showBackground = true)
fun PreferenceInfoPreview() {
    PreferenceInfo(text = "Title")
}

@Preview
@Composable
fun PreferencesHintCardPreview() {
    CompositionLocalProvider(LocalTonalPalettes provides Color.Green.toTonalPalettes()) {
        PreferencesHintCard(
            title = "Explore new features",
            icon = Icons.Outlined.TipsAndUpdates,
            description = "Find out what's new in this version",
            containerColor = FixedAccentColors.primaryFixed,
            contentColor = FixedAccentColors.onPrimaryFixed,
        )
    }
}