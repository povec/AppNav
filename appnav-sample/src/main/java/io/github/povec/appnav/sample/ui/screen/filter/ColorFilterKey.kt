package io.github.povec.appnav.sample.ui.screen.filter

import io.github.povec.appnav.key.AppNavArg
import io.github.povec.appnav.key.AppNavContext
import io.github.povec.appnav.key.AppNavKey
import io.github.povec.appnav.message.AppNavMessage
import io.github.povec.appnav.message.AppNavResult
import kotlinx.serialization.Serializable

@Serializable
data class ColorFilterKey(
    override val arg: ColorFilterArg,
    override val context: AppNavContext
): AppNavKey

@Serializable
data class ColorFilterArg(
    val red: Boolean,
    val green: Boolean,
    val blue: Boolean
): AppNavArg{
    override fun createKey(context: AppNavContext): AppNavKey = ColorFilterKey(this, context)
}

@Serializable
data class ColorFilterMessage(
    override val result: ColorFilterResult,
    override val payload: String?
): AppNavMessage

@Serializable
data class ColorFilterResult(
    val red: Boolean,
    val green: Boolean,
    val blue: Boolean
): AppNavResult {
    override fun createMessage(payload: String?): AppNavMessage = ColorFilterMessage(this, payload)
}