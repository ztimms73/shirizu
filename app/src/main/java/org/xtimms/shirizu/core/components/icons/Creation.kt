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

val Icons.Outlined.Creation: ImageVector
    get() {
        if (_creation != null) {
            return _creation!!
        }
        _creation = Builder(name = "Creation", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(9.0f, 4.0f)
                lineTo(11.5f, 9.5f)
                lineTo(17.0f, 12.0f)
                lineTo(11.5f, 14.5f)
                lineTo(9.0f, 20.0f)
                lineTo(6.5f, 14.5f)
                lineTo(1.0f, 12.0f)
                lineTo(6.5f, 9.5f)
                lineTo(9.0f, 4.0f)
                moveTo(9.0f, 8.83f)
                lineTo(8.0f, 11.0f)
                lineTo(5.83f, 12.0f)
                lineTo(8.0f, 13.0f)
                lineTo(9.0f, 15.17f)
                lineTo(10.0f, 13.0f)
                lineTo(12.17f, 12.0f)
                lineTo(10.0f, 11.0f)
                lineTo(9.0f, 8.83f)
                moveTo(19.0f, 9.0f)
                lineTo(17.74f, 6.26f)
                lineTo(15.0f, 5.0f)
                lineTo(17.74f, 3.75f)
                lineTo(19.0f, 1.0f)
                lineTo(20.25f, 3.75f)
                lineTo(23.0f, 5.0f)
                lineTo(20.25f, 6.26f)
                lineTo(19.0f, 9.0f)
                moveTo(19.0f, 23.0f)
                lineTo(17.74f, 20.26f)
                lineTo(15.0f, 19.0f)
                lineTo(17.74f, 17.75f)
                lineTo(19.0f, 15.0f)
                lineTo(20.25f, 17.75f)
                lineTo(23.0f, 19.0f)
                lineTo(20.25f, 20.26f)
                lineTo(19.0f, 23.0f)
                close()
            }
        }
        .build()
        return _creation!!
    }

private var _creation: ImageVector? = null
