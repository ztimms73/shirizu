package org.xtimms.shirizu.core.components.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Filled.Shirizu: ImageVector
    get() {
        if (_shirizu != null) {
            return _shirizu!!
        }
        _shirizu = Builder(name = "Shirizu", defaultWidth = 30.0.dp, defaultHeight = 30.0.dp,
                viewportWidth = 30.0f, viewportHeight = 30.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(9.1f, 17.6f)
                curveToRelative(-0.8f, -0.4f, -1.7f, -0.9f, -2.8f, -1.3f)
                curveToRelative(-1.1f, -0.4f, -2.2f, -0.8f, -3.3f, -1.2f)
                curveToRelative(-1.1f, -0.3f, -2.1f, -0.6f, -2.9f, -0.8f)
                lineToRelative(1.6f, -5.3f)
                curveToRelative(1.0f, 0.2f, 2.1f, 0.5f, 3.2f, 0.8f)
                curveToRelative(1.1f, 0.4f, 2.3f, 0.8f, 3.4f, 1.2f)
                curveToRelative(1.1f, 0.4f, 2.1f, 0.9f, 3.1f, 1.3f)
                lineTo(9.1f, 17.6f)
                close()
                moveTo(30.0f, 15.7f)
                curveToRelative(-1.0f, 1.5f, -2.2f, 3.0f, -3.6f, 4.4f)
                curveToRelative(-1.4f, 1.4f, -2.9f, 2.7f, -4.5f, 3.9f)
                curveToRelative(-1.6f, 1.2f, -3.2f, 2.3f, -4.9f, 3.2f)
                curveToRelative(-1.7f, 0.9f, -3.3f, 1.6f, -4.9f, 2.1f)
                curveTo(10.6f, 29.7f, 9.1f, 30.0f, 7.8f, 30.0f)
                curveToRelative(-1.8f, 0.0f, -3.4f, -0.6f, -4.8f, -1.7f)
                curveToRelative(-1.4f, -1.1f, -2.3f, -3.0f, -2.9f, -5.6f)
                lineTo(5.0f, 20.5f)
                curveToRelative(0.3f, 1.4f, 0.8f, 2.4f, 1.4f, 3.0f)
                curveToRelative(0.6f, 0.6f, 1.4f, 0.9f, 2.3f, 0.9f)
                curveToRelative(0.6f, 0.0f, 1.4f, -0.2f, 2.5f, -0.6f)
                curveToRelative(1.1f, -0.4f, 2.2f, -0.9f, 3.5f, -1.7f)
                curveToRelative(1.3f, -0.7f, 2.6f, -1.6f, 4.0f, -2.7f)
                curveToRelative(1.4f, -1.1f, 2.7f, -2.3f, 4.0f, -3.7f)
                curveToRelative(1.3f, -1.4f, 2.4f, -2.9f, 3.4f, -4.6f)
                lineTo(30.0f, 15.7f)
                close()
                moveTo(12.9f, 10.3f)
                curveToRelative(-1.0f, -0.9f, -2.3f, -1.9f, -4.0f, -2.8f)
                curveTo(7.3f, 6.6f, 5.6f, 5.8f, 3.8f, 5.0f)
                lineToRelative(1.9f, -5.0f)
                curveTo(7.0f, 0.4f, 8.2f, 1.0f, 9.5f, 1.6f)
                curveToRelative(1.2f, 0.6f, 2.4f, 1.3f, 3.5f, 2.0f)
                curveToRelative(1.1f, 0.7f, 2.0f, 1.3f, 2.7f, 2.0f)
                lineTo(12.9f, 10.3f)
                close()
            }
        }
        .build()
        return _shirizu!!
    }

private var _shirizu: ImageVector? = null
