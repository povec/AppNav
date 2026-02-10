package io.github.povec.appnav.sample.ui.screen.live

import io.github.povec.appnav.key.AppNavArg
import io.github.povec.appnav.key.AppNavContext
import io.github.povec.appnav.key.AppNavKey
import kotlinx.serialization.Serializable

@Serializable
data class ColorLiveViewerKey(
    override val arg: ColorLiveViewerArg,
    override val context: AppNavContext
): AppNavKey

@Serializable
data object ColorLiveViewerArg: AppNavArg{
    override fun createKey(context: AppNavContext): AppNavKey = ColorLiveViewerKey(this, context)
}