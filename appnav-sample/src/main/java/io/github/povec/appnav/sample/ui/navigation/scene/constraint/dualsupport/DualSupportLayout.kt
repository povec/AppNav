package io.github.povec.appnav.sample.ui.navigation.scene.constraint.dualsupport

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import io.github.povec.appnav.core.context
import io.github.povec.appnav.core.find
import io.github.povec.appnav.key.AppNavRole
import io.github.povec.appnav.key.AppNavSession
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.component.appbar.ContentWithScreenInfo
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.component.dialog.DialogContainer
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.metadata.DialogBehavior
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.metadata.Placeholder
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.metadata.Policy
import io.github.povec.appnav.sample.ui.navigation.stage.component.LocalTopInfo
import io.github.povec.appnav.sample.ui.navigation.strategy.compact.CompactStrategy
import io.github.povec.appnav.sample.ui.navigation.strategy.expand.ExpandStrategy
import io.github.povec.appnav.sample.ui.navigation.strategy.medium.MediumStrategy
import io.github.povec.appnav.scene.EntryPane
import io.github.povec.appnav.scene.OverlayEntryPane
import io.github.povec.appnav.scene.appNavConstraintLayout

sealed interface DualSupportLayoutEvent {
    data class DismissDialog(val session: AppNavSession): DualSupportLayoutEvent
}

val dualSupportLayout = appNavConstraintLayout<DualSupportLayoutEvent>(DualSupportRole.CONSTRAINT) {
    presentation(
        paneCount = 1,
        CompactStrategy.NAME
    ) { emitter ->
        val info = LocalTopInfo.current
        val showNavigation = info.isShowNavigation

        EntryPane(
            modifier = Modifier.fillMaxSize(),
            panePriority = 0,
            placeholder = null,
        ) {

            val role  = context.role

            ContentWithScreenInfo(
                showNavigation = showNavigation,
                policy = roleRootMetadata(role).find(Policy.KEY, role, constraint),
            )

        }

        OverlayEntryPane(
            modifier = Modifier,
            overlayContainer = {
                DialogContainer(
                    dialogBehavior = roleTopMetadata(AppNavRole.Base).find(DialogBehavior.KEY, DualSupportRole.DIALOG, constraint.id)?: DialogBehavior.Overlay,
                    onDismiss = { emitter.send(DualSupportLayoutEvent.DismissDialog(activeSession)) }
                ){
                    it.invoke()
                }
            }
        ) {
            ContentWithScreenInfo(
                showNavigation = showNavigation,
                policy = roleRootMetadata(AppNavRole.Overlay).find(Policy.KEY, DualSupportRole.DIALOG, constraint.id )
            )
        }
    }

    presentation(
        paneCount = 2,
        MediumStrategy.NAME
    ) { emitter ->
        val info = LocalTopInfo.current
        val showNavigation = info.isShowNavigation

        Row(Modifier.fillMaxSize()) {

            val baseMetadata = roleRootMetadata(AppNavRole.Base)

            val support1Placeholder: Placeholder? =
                baseMetadata.find(Placeholder.KEY, DualSupportRole.SUPPORT1, constraint.id)

            EntryPane(
                modifier = Modifier.weight(1f),
                panePriority = 0,
                placeholder = null,
            ) {

                val role = context.role

                ContentWithScreenInfo(
                    showNavigation = showNavigation,
                    policy = roleRootMetadata(role).find(Policy.KEY, role, constraint),
                )

            }

            EntryPane(
                modifier = Modifier.weight(1f),
                panePriority = 1,
                placeholder = support1Placeholder?.content,
            ) {

                val role  = context.role

                ContentWithScreenInfo(
                    showNavigation = showNavigation,
                    policy = roleRootMetadata(role).find(Policy.KEY, role, constraint),
                )

            }
        }

        OverlayEntryPane(
            modifier = Modifier,
            overlayContainer = {
                DialogContainer(
                    dialogBehavior = roleTopMetadata(AppNavRole.Overlay).find(DialogBehavior.KEY, DualSupportRole.DIALOG, constraint.id)?: DialogBehavior.Overlay,
                    onDismiss = { emitter.send(DualSupportLayoutEvent.DismissDialog(activeSession)) }
                ){
                    it.invoke()
                }
            }
        ) {
            ContentWithScreenInfo(
                showNavigation = showNavigation,
                policy = roleRootMetadata(AppNavRole.Overlay).find(Policy.KEY, DualSupportRole.DIALOG, constraint.id )
            )
        }
    }

    presentation(
        paneCount = 3,
        ExpandStrategy.NAME,
    ) { emitter ->
        val info = LocalTopInfo.current
        val showNavigation = info.isShowNavigation

        Row(Modifier.fillMaxSize()) {

            val baseMetadata = roleRootMetadata(AppNavRole.Base)

            val support1PlaceHolder: Placeholder? =
                baseMetadata.find(
                    Placeholder.KEY,
                    DualSupportRole.SUPPORT1,
                    constraint.id,
                )
            val support2PlaceHolder: Placeholder? =
                baseMetadata.find(
                    Placeholder.KEY,
                    DualSupportRole.SUPPORT2,
                    constraint.id,
                )

            EntryPane(
                modifier = Modifier.weight(1f),
                panePriority = 2,
                placeholder = support2PlaceHolder?.content,
            ) {

                val role  = context.role

                ContentWithScreenInfo(
                    showNavigation = showNavigation,
                    policy = roleRootMetadata(role).find(Policy.KEY, role, constraint),
                )

            }

            EntryPane(
                modifier = Modifier.weight(1f),
                panePriority = 0,
                placeholder = null,
            ) {

                val role = context.role

                ContentWithScreenInfo(
                    showNavigation = showNavigation,
                    policy = roleRootMetadata(role).find(Policy.KEY, role, constraint),
                )

            }

            EntryPane(
                modifier = Modifier.weight(1f),
                panePriority = 1,
                placeholder = support1PlaceHolder?.content,
            ) {

                val role  = context.role

                ContentWithScreenInfo(
                    showNavigation = showNavigation,
                    policy = roleRootMetadata(role).find(Policy.KEY, role, constraint),
                )

            }
        }

        OverlayEntryPane(
            modifier = Modifier,
            overlayContainer = {
                DialogContainer(
                    dialogBehavior = roleTopMetadata(AppNavRole.Overlay).find(DialogBehavior.KEY, DualSupportRole.DIALOG, constraint.id)?: DialogBehavior.Overlay,
                    onDismiss = { emitter.send(DualSupportLayoutEvent.DismissDialog(activeSession)) }
                ){
                    it.invoke()
                }
            }
        ) {
            ContentWithScreenInfo(
                showNavigation = showNavigation,
                policy = roleRootMetadata(AppNavRole.Overlay).find(Policy.KEY, DualSupportRole.DIALOG, constraint.id )
            )
        }
    }
}