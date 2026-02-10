package io.github.povec.appnav.sample.ui.navigation.strategy

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.scene.SceneStrategy
import androidx.navigationevent.NavigationEvent
import io.github.povec.appnav.constraint.AppNavConstraintResolver
import io.github.povec.appnav.key.AppNavKey
import io.github.povec.appnav.sample.ui.navigation.strategy.compact.CompactStrategy
import io.github.povec.appnav.sample.ui.navigation.strategy.expand.ExpandStrategy
import io.github.povec.appnav.sample.ui.navigation.strategy.medium.MediumStrategy
import io.github.povec.appnav.scene.AppNavSceneLayout
import io.github.povec.appnav.scene.AppNavTransitionValue

@Composable
fun rememberAppNavStrategy(
    directive: PaneScaffoldDirective,
    constraintResolver: AppNavConstraintResolver,
    layouts: List<AppNavSceneLayout>,
    sizeTransform: SizeTransform?,
    enterTransitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.() -> ContentTransform,
    exitTransitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.() -> ContentTransform,
    popTransitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.() -> ContentTransform,
    transitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.() -> ContentTransform,
    predictivePopTransitionSpec: AnimatedContentTransitionScope<AppNavTransitionValue>.(@NavigationEvent.SwipeEdge Int) -> ContentTransform,
): SceneStrategy<AppNavKey> = remember(directive, constraintResolver, layouts) {

    val strategies = listOf(
        CompactStrategy(
            directive = directive,
            resolver = constraintResolver,
            layouts = layouts,
            sizeTransform = sizeTransform,
            enterTransitionSpec = enterTransitionSpec,
            exitTransitionSpec = exitTransitionSpec,
            popTransitionSpec = popTransitionSpec,
            transitionSpec = transitionSpec,
            predictivePopTransitionSpec = predictivePopTransitionSpec
        ),
        MediumStrategy(
            directive = directive,
            resolver = constraintResolver,
            layouts = layouts,
            sizeTransform = sizeTransform,
            enterTransitionSpec = enterTransitionSpec,
            exitTransitionSpec = exitTransitionSpec,
            popTransitionSpec = popTransitionSpec,
            transitionSpec = transitionSpec,
            predictivePopTransitionSpec = predictivePopTransitionSpec
        ),
        ExpandStrategy(
            directive = directive,
            resolver = constraintResolver,
            layouts = layouts,
            sizeTransform = sizeTransform,
            enterTransitionSpec = enterTransitionSpec,
            exitTransitionSpec = exitTransitionSpec,
            popTransitionSpec = popTransitionSpec,
            transitionSpec = transitionSpec,
            predictivePopTransitionSpec = predictivePopTransitionSpec
        ),
    ).reversed()

    SceneStrategy { entries ->
        strategies.firstNotNullOfOrNull { strategy -> with(strategy) { calculateScene(entries) } }
    }

}