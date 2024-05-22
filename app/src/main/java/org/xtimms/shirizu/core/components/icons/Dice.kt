package org.xtimms.shirizu.core.components.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val Icons.Outlined.Dice: ImageVector
    get() {
        if (_dice != null) {
            return _dice!!
        }
        _dice = materialIcon(name = "Outlined.Dice") {
            materialPath {
                moveTo(19.0f, 5.0f)
                verticalLineTo(19.0f)
                horizontalLineTo(5.0f)
                verticalLineTo(5.0f)
                horizontalLineTo(19.0f)
                moveTo(19.0f, 3.0f)
                horizontalLineTo(5.0f)
                curveTo(3.9f, 3.0f, 3.0f, 3.9f, 3.0f, 5.0f)
                verticalLineTo(19.0f)
                curveTo(3.0f, 20.1f, 3.9f, 21.0f, 5.0f, 21.0f)
                horizontalLineTo(19.0f)
                curveTo(20.1f, 21.0f, 21.0f, 20.1f, 21.0f, 19.0f)
                verticalLineTo(5.0f)
                curveTo(21.0f, 3.9f, 20.1f, 3.0f, 19.0f, 3.0f)
                moveTo(7.5f, 6.0f)
                curveTo(6.7f, 6.0f, 6.0f, 6.7f, 6.0f, 7.5f)
                reflectiveCurveTo(6.7f, 9.0f, 7.5f, 9.0f)
                reflectiveCurveTo(9.0f, 8.3f, 9.0f, 7.5f)
                reflectiveCurveTo(8.3f, 6.0f, 7.5f, 6.0f)
                moveTo(16.5f, 15.0f)
                curveTo(15.7f, 15.0f, 15.0f, 15.7f, 15.0f, 16.5f)
                curveTo(15.0f, 17.3f, 15.7f, 18.0f, 16.5f, 18.0f)
                curveTo(17.3f, 18.0f, 18.0f, 17.3f, 18.0f, 16.5f)
                curveTo(18.0f, 15.7f, 17.3f, 15.0f, 16.5f, 15.0f)
                moveTo(16.5f, 6.0f)
                curveTo(15.7f, 6.0f, 15.0f, 6.7f, 15.0f, 7.5f)
                reflectiveCurveTo(15.7f, 9.0f, 16.5f, 9.0f)
                curveTo(17.3f, 9.0f, 18.0f, 8.3f, 18.0f, 7.5f)
                reflectiveCurveTo(17.3f, 6.0f, 16.5f, 6.0f)
                moveTo(12.0f, 10.5f)
                curveTo(11.2f, 10.5f, 10.5f, 11.2f, 10.5f, 12.0f)
                reflectiveCurveTo(11.2f, 13.5f, 12.0f, 13.5f)
                reflectiveCurveTo(13.5f, 12.8f, 13.5f, 12.0f)
                reflectiveCurveTo(12.8f, 10.5f, 12.0f, 10.5f)
                moveTo(7.5f, 15.0f)
                curveTo(6.7f, 15.0f, 6.0f, 15.7f, 6.0f, 16.5f)
                curveTo(6.0f, 17.3f, 6.7f, 18.0f, 7.5f, 18.0f)
                reflectiveCurveTo(9.0f, 17.3f, 9.0f, 16.5f)
                curveTo(9.0f, 15.7f, 8.3f, 15.0f, 7.5f, 15.0f)
                close()
            }
        }
        return _dice!!
    }

private var _dice: ImageVector? = null