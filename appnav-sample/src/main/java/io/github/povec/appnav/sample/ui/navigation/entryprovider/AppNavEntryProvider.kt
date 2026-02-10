package io.github.povec.appnav.sample.ui.navigation.entryprovider

import androidx.navigation3.runtime.entryProvider
import io.github.povec.appnav.core.entryWithController
import io.github.povec.appnav.core.appNavMetadata
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.metadata.Placeholder
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.dualsupport.DualSupportRole
import io.github.povec.appnav.sample.ui.screen.editor.ColorEditorKey
import io.github.povec.appnav.sample.ui.screen.editor.ColorEditorScreen
import io.github.povec.appnav.sample.ui.screen.editor.ColorEditor_LiveViewerPlaceholder
import io.github.povec.appnav.sample.ui.screen.extra.ColorExtraKey
import io.github.povec.appnav.sample.ui.screen.extra.ColorExtraScreen
import io.github.povec.appnav.sample.ui.screen.filter.ColorFilterKey
import io.github.povec.appnav.sample.ui.screen.filter.ColorFilterScreen
import io.github.povec.appnav.sample.ui.screen.home.HomeKey
import io.github.povec.appnav.sample.ui.screen.home.HomeScreen
import io.github.povec.appnav.sample.ui.screen.list.ColorListKey
import io.github.povec.appnav.sample.ui.screen.list.ColorListScreen
import io.github.povec.appnav.sample.ui.screen.live.ColorLiveViewerKey
import io.github.povec.appnav.sample.ui.screen.live.ColorLiveViewerScreen
import io.github.povec.appnav.sample.ui.screen.selector.ColorSelectorKey
import io.github.povec.appnav.sample.ui.screen.selector.ColorSelectorScreen
import io.github.povec.appnav.sample.ui.screen.viewer.ColorViewerKey
import io.github.povec.appnav.sample.ui.screen.viewer.ColorViewerScreen

val appNavEntryProvider = entryProvider {
    entryWithController<HomeKey> { HomeScreen() }

    entryWithController<ColorEditorKey>(
        metadata = appNavMetadata {
            constraint(DualSupportRole.CONSTRAINT) {
                role(DualSupportRole.SUPPORT1){
                    this[Placeholder.KEY] = Placeholder(content = { ColorEditor_LiveViewerPlaceholder() })
                }
            }
        }
    ) { ColorEditorScreen() }
    entryWithController<ColorSelectorKey> { (arg) -> ColorSelectorScreen() }
    entryWithController<ColorLiveViewerKey> { ColorLiveViewerScreen() }
    entryWithController<ColorFilterKey> { ColorFilterScreen() }

    entryWithController<ColorListKey> { ColorListScreen() }
    entryWithController<ColorViewerKey> { (arg) -> ColorViewerScreen(arg = arg) }
    entryWithController<ColorExtraKey> { (arg) -> ColorExtraScreen(arg = arg) }
}