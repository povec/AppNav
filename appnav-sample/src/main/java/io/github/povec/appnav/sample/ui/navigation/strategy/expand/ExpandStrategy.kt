package io.github.povec.appnav.sample.ui.navigation.strategy.expand

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.navigation3.runtime.NavEntry
import androidx.navigationevent.NavigationEvent
import io.github.povec.appnav.constraint.AppNavConstraintResolver
import io.github.povec.appnav.key.AppNavKey
import io.github.povec.appnav.scene.AppNavSceneLayout
import io.github.povec.appnav.scene.AppNavSceneStrategy
import io.github.povec.appnav.scene.AppNavTransitionValue

class ExpandStrategy(
    val directive: PaneScaffoldDirective,
    override val resolver: AppNavConstraintResolver,
    override val layouts: List<AppNavSceneLayout>,
    override val sizeTransform: SizeTransform?,
    override val enterTransitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.() -> ContentTransform,
    override val exitTransitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.() -> ContentTransform,
    override val transitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.() -> ContentTransform,
    override val popTransitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.() -> ContentTransform,
    override val predictivePopTransitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.(@NavigationEvent.SwipeEdge Int) -> ContentTransform,
): AppNavSceneStrategy() {

    override val name: String = NAME
    
    override fun calculateResolve(entries: List<NavEntry<AppNavKey>>): Boolean {
        return directive.maxHorizontalPartitions == 3
    }
    
    companion object {
        const val NAME = "Expand"
    }

}