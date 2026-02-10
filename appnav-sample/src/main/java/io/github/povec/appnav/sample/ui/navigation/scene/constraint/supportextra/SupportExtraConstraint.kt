package io.github.povec.appnav.sample.ui.navigation.scene.constraint.supportextra

import io.github.povec.appnav.constraint.appNavConstraint

object SupportExtraRole {
    const val CONSTRAINT = "SupportExtraConstraint"

    const val MAIN = "main"
    const val SUPPORT = "support"
    const val EXTRA = "extra"
    const val DIALOG = "dialog"

}

val supportExtraConstraint = appNavConstraint(
    id = SupportExtraRole.CONSTRAINT,
    base = SupportExtraRole.MAIN,
    overlay = SupportExtraRole.DIALOG,
){
    pane(SupportExtraRole.SUPPORT){
        pane(SupportExtraRole.EXTRA)
    }
}