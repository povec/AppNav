package io.github.povec.appnav.sample.ui.screen.list

import io.github.povec.appnav.key.AppNavArg
import io.github.povec.appnav.key.AppNavContext
import io.github.povec.appnav.key.AppNavKey
import kotlinx.serialization.Serializable

@Serializable
data class ColorListKey(
    override val arg: ColorListArg,
    override val context: AppNavContext
): AppNavKey

@Serializable
data object ColorListArg: AppNavArg{
    override fun createKey(context: AppNavContext): AppNavKey = ColorListKey(this, context)
}