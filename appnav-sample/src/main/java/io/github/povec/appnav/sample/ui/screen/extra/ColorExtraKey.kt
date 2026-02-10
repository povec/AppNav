package io.github.povec.appnav.sample.ui.screen.extra

import io.github.povec.appnav.key.AppNavArg
import io.github.povec.appnav.key.AppNavContext
import io.github.povec.appnav.key.AppNavKey
import io.github.povec.appnav.sample.ui.data.RGBColor
import kotlinx.serialization.Serializable

@Serializable
data class ColorExtraKey(
    override val arg: ColorExtraArg,
    override val context: AppNavContext
): AppNavKey

@Serializable
data class ColorExtraArg(
    val rgbColor: RGBColor.Item
): AppNavArg{
    override fun createKey(context: AppNavContext): AppNavKey = ColorExtraKey(this, context)
}