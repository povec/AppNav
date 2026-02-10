package io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.metadata

sealed interface DialogBehavior {

    data object Overlay: DialogBehavior

    data object BottomSheet: DialogBehavior

    companion object {
        const val KEY: String = "dialogBehavior"

        val DEFAULT = Overlay

    }

}