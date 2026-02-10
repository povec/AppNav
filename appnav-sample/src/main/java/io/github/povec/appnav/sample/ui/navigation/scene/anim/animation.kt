package io.github.povec.appnav.sample.ui.navigation.scene.anim

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

internal const val DEFAULT_TRANSITION_DURATION_MILLISECOND = 700

// 1. 通常の「進む」アニメーション
fun <T> enterTransitionSpec(): AnimatedContentTransitionScope<T>.() -> ContentTransform = {
    ContentTransform(
        fadeIn(animationSpec = tween(DEFAULT_TRANSITION_DURATION_MILLISECOND)),
        fadeOut(animationSpec = tween(DEFAULT_TRANSITION_DURATION_MILLISECOND)),
    )
}

// 1. 通常の「進む」アニメーション
fun <T> exitTransitionSpec(): AnimatedContentTransitionScope<T>.() -> ContentTransform = {
    ContentTransform(
        fadeIn(animationSpec = tween(DEFAULT_TRANSITION_DURATION_MILLISECOND)),
        fadeOut(animationSpec = tween(DEFAULT_TRANSITION_DURATION_MILLISECOND)),
    )
}

// 1. 通常の「進む」アニメーション
fun <T> transitionSpec(): AnimatedContentTransitionScope<T>.() -> ContentTransform = {
    ContentTransform(
        fadeIn(animationSpec = tween(DEFAULT_TRANSITION_DURATION_MILLISECOND)),
        fadeOut(animationSpec = tween(DEFAULT_TRANSITION_DURATION_MILLISECOND)),
    )
}

// 2. 通常の「戻る」アニメーション
fun <T> popTransitionSpec(): AnimatedContentTransitionScope<T>.() -> ContentTransform = {
    ContentTransform(
        fadeIn(animationSpec = tween(DEFAULT_TRANSITION_DURATION_MILLISECOND)),
        fadeOut(animationSpec = tween(DEFAULT_TRANSITION_DURATION_MILLISECOND)),
    )
}

// 3. 予測バック（ジェスチャー同期）アニメーション
fun <T> predictivePopTransitionSpec(): AnimatedContentTransitionScope<T>.(Int) -> ContentTransform = { edge ->

    ContentTransform(
        fadeIn(animationSpec = tween(DEFAULT_TRANSITION_DURATION_MILLISECOND)),
        fadeOut(animationSpec = tween(DEFAULT_TRANSITION_DURATION_MILLISECOND)),
    )

}