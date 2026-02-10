package io.github.povec.appnav.sample.ui.navigation.stage.component

import androidx.compose.runtime.staticCompositionLocalOf

val LocalTopInfo = staticCompositionLocalOf<TopInfo> {
    error("CompositionLocal LocalStandardInfo not present")
}

data class TopInfo(
    val isShowNavigation: Boolean,
)