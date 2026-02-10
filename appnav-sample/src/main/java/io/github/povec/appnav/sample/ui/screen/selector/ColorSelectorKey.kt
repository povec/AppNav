package io.github.povec.appnav.sample.ui.screen.selector

import io.github.povec.appnav.key.AppNavArg
import io.github.povec.appnav.key.AppNavContext
import io.github.povec.appnav.key.AppNavKey
import io.github.povec.appnav.message.AppNavMessage
import io.github.povec.appnav.message.AppNavResult
import io.github.povec.appnav.sample.ui.data.RGBColor
import kotlinx.serialization.Serializable

@Serializable
data class ColorSelectorKey(
    override val arg: ColorSelectorArg,
    override val context: AppNavContext
): AppNavKey

@Serializable
data object ColorSelectorArg: AppNavArg{
    override fun createKey(context: AppNavContext): AppNavKey = ColorSelectorKey(this, context)
}

@Serializable
data class ColorSelectorMessage(
    override val result: ColorSelectorResult,
    override val payload: String?
): AppNavMessage

@Serializable
data class ColorSelectorResult(
    val rgbColor: RGBColor.Item,
): AppNavResult {
    override fun createMessage(payload: String?): AppNavMessage = ColorSelectorMessage(this, payload)
}