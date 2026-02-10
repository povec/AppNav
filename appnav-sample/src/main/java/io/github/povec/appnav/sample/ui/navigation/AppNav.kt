package io.github.povec.appnav.sample.ui.navigation
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.HingePolicy
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.runtime.Composable
import io.github.povec.appnav.AppNavDisplay
import io.github.povec.appnav.key.AppNavRole
import io.github.povec.appnav.scene.injectEvent
import io.github.povec.appnav.core.rememberAppNavBackStack
import io.github.povec.appnav.sample.ui.navigation.register.ConstSession
import io.github.povec.appnav.sample.ui.navigation.register.appNavRegistry
import io.github.povec.appnav.sample.ui.navigation.scene.anim.enterTransitionSpec
import io.github.povec.appnav.sample.ui.navigation.scene.anim.popTransitionSpec
import io.github.povec.appnav.sample.ui.navigation.scene.anim.predictivePopTransitionSpec
import io.github.povec.appnav.sample.ui.navigation.scene.anim.transitionSpec
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.constraintResolver
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.supportextra.SupportExtraLayoutEvent
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.supportextra.supportExtraLayout
import io.github.povec.appnav.sample.ui.navigation.entryprovider.appNavEntryProvider
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.dualsupport.DualSupportLayoutEvent
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.dualsupport.dualSupportLayout
import io.github.povec.appnav.sample.ui.navigation.stage.AppNavStage
import io.github.povec.appnav.sample.ui.navigation.strategy.rememberAppNavStrategy

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AppNav(
    onFinish: () -> Unit,
){
    val constraintResolver = constraintResolver

    val registryKeyProvider = appNavRegistry.resolve(constraintResolver)

    val backStack = rememberAppNavBackStack(
        registryKeyProvider(ConstSession.HOME)
    )

    val layouts =
        buildList {
            addAll(dualSupportLayout)
            addAll(supportExtraLayout)
        }
        .injectEvent {
            on<SupportExtraLayoutEvent> {
                when(it){
                    is SupportExtraLayoutEvent.DismissDialog -> {
                        backStack.excludeRole(it.session, AppNavRole.Overlay)
                    }
                }
            }
            on<DualSupportLayoutEvent> {
                when(it){
                    is DualSupportLayoutEvent.DismissDialog -> {
                        backStack.excludeRole(it.session, AppNavRole.Overlay)
                    }
                }
            }
        }

    val info = currentWindowAdaptiveInfo()
    val directive = calculatePaneScaffoldDirective(info, HingePolicy.AlwaysAvoid)

    val strategy = rememberAppNavStrategy(
        constraintResolver = constraintResolver,
        layouts = layouts,
        directive = directive,
        sizeTransform = null,
        enterTransitionSpec = enterTransitionSpec(),
        exitTransitionSpec = enterTransitionSpec(),
        popTransitionSpec = popTransitionSpec(),
        transitionSpec = transitionSpec(),
        predictivePopTransitionSpec = predictivePopTransitionSpec()
    )

    AppNavStage(
        activeSession = backStack.activeSession,
        directive = directive,
        onNavigation = { backStack.navigate(registryKeyProvider(it)) },
    ) {
        AppNavDisplay(
            backStack = backStack,
            registerKeyProvider = registryKeyProvider,
            entryProvider = appNavEntryProvider,
            constraintResolver = constraintResolver,
            sceneStrategy = strategy,
            transitionSpec = transitionSpec(),
            popTransitionSpec = popTransitionSpec(),
            predictivePopTransitionSpec = predictivePopTransitionSpec(),
            onFinish = onFinish,
        )
    }

}