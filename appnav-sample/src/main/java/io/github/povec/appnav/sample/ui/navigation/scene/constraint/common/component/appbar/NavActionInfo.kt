package io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.component.appbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.NavEntry
import io.github.povec.appnav.core.context
import io.github.povec.appnav.key.AppNavCaller
import io.github.povec.appnav.key.AppNavKey
import io.github.povec.appnav.key.AppNavRole
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.metadata.Policy
import io.github.povec.appnav.sample.ui.navigation.stage.component.TopAppBarNavType

val LocalNavActionInfo = staticCompositionLocalOf { NavActionInfo(TopAppBarNavType.BACK) }

data class NavActionInfo(
    val navIconType: TopAppBarNavType
)

/**
 * ScreenInfo を合成して Provide する
 */
@Composable
fun NavEntry<AppNavKey>.ContentWithScreenInfo(
    showNavigation: Boolean,
    policy: Policy?,
) {
    val meta = context

    val isCall = meta.caller != AppNavCaller.EMPTY

    val icon = when(meta.isRoleRoot){
        AppNavRole.Base if(showNavigation) -> TopAppBarNavType.NONE
        null -> TopAppBarNavType.BACK
        else -> (policy?: Policy.DEFAULT).select(isCall)
    }

    val info = NavActionInfo(navIconType = icon)

    CompositionLocalProvider(LocalNavActionInfo provides info) {
        Content()
    }

}