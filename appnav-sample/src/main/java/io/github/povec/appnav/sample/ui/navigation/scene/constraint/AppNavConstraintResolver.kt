package io.github.povec.appnav.sample.ui.navigation.scene.constraint

import io.github.povec.appnav.constraint.constraintResolver
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.dualsupport.dualSupportConstraint
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.supportextra.supportExtraConstraint
import io.github.povec.appnav.sample.ui.screen.editor.ColorEditorArg

val constraintResolver = constraintResolver {

    bind<ColorEditorArg>(dualSupportConstraint)

    otherwise(supportExtraConstraint)

}