package io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.metadata

import io.github.povec.appnav.sample.ui.navigation.stage.component.TopAppBarNavType

data class Policy(
    val icon: TopAppBarNavType = TopAppBarNavType.BACK,
    val whenCallIcon: TopAppBarNavType = TopAppBarNavType.CLOSE,
){
    fun select(isCall: Boolean): TopAppBarNavType = if(isCall) whenCallIcon else icon

    companion object {

        const val KEY: String = "policy"

        val DEFAULT = Policy(
            icon = TopAppBarNavType.BACK,
            whenCallIcon = TopAppBarNavType.CLOSE,
        )
    }
}