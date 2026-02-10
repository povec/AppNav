package io.github.povec.appnav.sample.ui.navigation.scene.constraint.dualsupport

import io.github.povec.appnav.constraint.appNavConstraint

object DualSupportRole {
    const val CONSTRAINT = "DualSupportConstraint"

    const val MAIN = "main"
    const val SUPPORT1 = "support1"
    const val SUPPORT2 = "support2"
    const val DIALOG = "dialog"

}

val dualSupportConstraint = appNavConstraint(
    id = DualSupportRole.CONSTRAINT,
    base = DualSupportRole.MAIN,
    overlay = DualSupportRole.DIALOG,
){
    pane(DualSupportRole.SUPPORT1)
    pane(DualSupportRole.SUPPORT2)
}