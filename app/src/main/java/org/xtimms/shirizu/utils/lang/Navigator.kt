package org.xtimms.shirizu.utils.lang

import android.graphics.Path
import android.view.animation.PathInterpolator
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.ScreenModelStore
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.core.stack.StackEvent
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.ScreenTransition
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.plus

interface Tab : cafe.adriel.voyager.navigator.tab.Tab {
    suspend fun onReselect(navigator: Navigator) {}
}

abstract class Screen : Screen {

    override val key: ScreenKey = uniqueScreenKey
}

const val DURATION_ENTER = 400
const val DURATION_EXIT = 200
const val initialOffset = 0.10f

fun PathInterpolator.toEasing(): Easing {
    return Easing { f -> this.getInterpolation(f) }
}

val path = Path().apply {
    moveTo(0f, 0f)
    cubicTo(0.05F, 0F, 0.133333F, 0.06F, 0.166666F, 0.4F)
    cubicTo(0.208333F, 0.82F, 0.25F, 1F, 1F, 1F)
}

val emphasizePathInterpolator = PathInterpolator(path)
val emphasizeEasing = emphasizePathInterpolator.toEasing()

val enterTween = tween<IntOffset>(durationMillis = DURATION_ENTER, easing = emphasizeEasing)
val exitTween = tween<IntOffset>(durationMillis = DURATION_ENTER, easing = emphasizeEasing)
val fadeTween = tween<Float>(durationMillis = DURATION_EXIT)

val enter =
    slideInHorizontally(enterTween, initialOffsetX = { (it * initialOffset).toInt() }) + fadeIn(
        fadeTween
    )
val exit =
    slideOutHorizontally(exitTween, targetOffsetX = { -(it * initialOffset).toInt() }) + fadeOut(
        fadeTween
    )

fun materialSharedAxisXIn(
    forward: Boolean,
): EnterTransition =
    slideInHorizontally(
        enterTween,
        initialOffsetX = {
            if (forward) (it * initialOffset).toInt() else -(it * initialOffset).toInt()
        }
    ) + fadeIn(fadeTween)

fun materialSharedAxisXOut(
    forward: Boolean,
): ExitTransition =
    slideOutHorizontally(
        exitTween,
        targetOffsetX = {
            if (forward) -(it * initialOffset).toInt() else (it * initialOffset).toInt()
        }
    ) + fadeOut(fadeTween)

fun materialSharedAxisX(
    forward: Boolean,
): ContentTransform = materialSharedAxisXIn(
    forward = forward,
) togetherWith materialSharedAxisXOut(
    forward = forward,
)

@Composable
fun DefaultNavigatorScreenTransition(navigator: Navigator) {
    ScreenTransition(
        navigator = navigator,
        transition = {
            materialSharedAxisX(
                forward = navigator.lastEvent != StackEvent.Pop,
            )
        },
    )
}

val ScreenModel.ioCoroutineScope: CoroutineScope
    get() = ScreenModelStore.getOrPutDependency(
        screenModel = this,
        name = "ScreenModelIoCoroutineScope",
        factory = { key -> CoroutineScope(Dispatchers.IO + SupervisorJob()) + CoroutineName(key) },
        onDispose = { scope -> scope.cancel() },
    )

interface AssistContentScreen {
    fun onProvideAssistUrl(): String?
}

interface NoLiftingAppBarScreen