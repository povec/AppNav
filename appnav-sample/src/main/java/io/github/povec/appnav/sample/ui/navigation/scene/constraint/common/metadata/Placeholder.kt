package io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.metadata

import androidx.compose.runtime.Composable

data class Placeholder(
    val content: @Composable () -> Unit,
){
    companion object {

        const val KEY: String = "placeholder"

    }
}