package io.github.povec.appnav.sample.ui.screen.viewer

import io.github.povec.appnav.key.AppNavArg
import io.github.povec.appnav.key.AppNavContext
import io.github.povec.appnav.key.AppNavKey
import io.github.povec.appnav.sample.ui.data.RGBColor
import kotlinx.serialization.Serializable

@Serializable
data class ColorViewerKey(
    override val arg: ColorViewerArg,
    override val context: AppNavContext
): AppNavKey

@Serializable
data class ColorViewerArg(
    val rgbColor: RGBColor.Item
): AppNavArg{
    override fun createKey(context: AppNavContext): AppNavKey = ColorViewerKey(this, context)
}