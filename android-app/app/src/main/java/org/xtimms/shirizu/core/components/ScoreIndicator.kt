package org.xtimms.shirizu.core.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.xtimms.shirizu.R
import org.xtimms.shirizu.ui.theme.ShirizuTheme
import org.xtimms.shirizu.utils.lang.toStringPositiveValueOrUnknown

@Composable
fun SmallScoreIndicator(
    score: Float?,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.StarOutline,
            contentDescription = stringResource(R.string.mean_score),
            tint = MaterialTheme.colorScheme.outline
        )
        if (score != null) {
            Text(
                text = (score.times(5.0F)).toStringPositiveValueOrUnknown(),
                modifier = Modifier.padding(horizontal = 4.dp),
                color = MaterialTheme.colorScheme.outline,
                fontSize = fontSize
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SmallScoreIndicatorPreview() {
    ShirizuTheme {
        SmallScoreIndicator(score = 1f)
    }
}