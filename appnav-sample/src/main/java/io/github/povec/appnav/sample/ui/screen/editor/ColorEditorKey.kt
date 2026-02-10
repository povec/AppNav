package io.github.povec.appnav.sample.ui.screen.editor

import io.github.povec.appnav.key.AppNavArg
import io.github.povec.appnav.key.AppNavContext
import io.github.povec.appnav.key.AppNavKey
import kotlinx.serialization.Serializable

@Serializable
data class ColorEditorKey(
    override val arg: ColorEditorArg,
    override val context: AppNavContext
): AppNavKey

@Serializable
data object ColorEditorArg: AppNavArg{
    override fun createKey(context: AppNavContext): AppNavKey = ColorEditorKey(this, context)
}