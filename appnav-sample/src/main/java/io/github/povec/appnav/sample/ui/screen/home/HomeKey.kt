package io.github.povec.appnav.sample.ui.screen.home

import io.github.povec.appnav.key.AppNavArg
import io.github.povec.appnav.key.AppNavContext
import io.github.povec.appnav.key.AppNavKey
import kotlinx.serialization.Serializable

@Serializable
data class HomeKey(
    override val arg: HomeArg,
    override val context: AppNavContext
): AppNavKey

@Serializable
data object HomeArg: AppNavArg{
    override fun createKey(context: AppNavContext): AppNavKey = HomeKey(this, context)
}