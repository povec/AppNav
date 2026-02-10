package io.github.povec.appnav.sample.ui.navigation.stage

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import io.github.povec.appnav.key.AppNavSession
import io.github.povec.appnav.sample.ui.navigation.register.navigationItems
import io.github.povec.appnav.sample.ui.navigation.stage.component.LocalTopInfo
import io.github.povec.appnav.sample.ui.navigation.stage.component.NavLocation
import io.github.povec.appnav.sample.ui.navigation.stage.component.TopInfo

@Composable
fun AppNavStage(
    activeSession: AppNavSession?,
    directive: PaneScaffoldDirective,
    onNavigation: (identifier: String) -> Unit,
    content: @Composable () -> Unit
){

    val navSuiteType = when (directive.maxHorizontalPartitions) {
        1 -> NavigationSuiteType.NavigationBar
        2 -> NavigationSuiteType.NavigationRail
        3 -> NavigationSuiteType.NavigationDrawer
        else -> NavigationSuiteType.None
    }

    val location = when(navSuiteType){
        NavigationSuiteType.NavigationBar -> NavLocation.BOTTOM_BAR
        NavigationSuiteType.NavigationRail -> NavLocation.BOTTOM_BAR or NavLocation.RAIL
        NavigationSuiteType.NavigationDrawer -> NavLocation.BOTTOM_BAR or NavLocation.RAIL or NavLocation.DRAWER
        else -> NavLocation.NONE
    }

    val visible = navigationItems.any { it.visible(location) && it.identifier == activeSession?.identifier }

    NavigationSuiteScaffold(
        layoutType = if(visible) navSuiteType else NavigationSuiteType.None,
        navigationSuiteItems = {
            navigationItems
                .filter{ it.exist(location) }
                .forEach { item ->
                    // 現在選択されているか判定
                    val selected = activeSession?.identifier == item.identifier

                    item(
                        selected = selected && !item.actionFlag,
                        onClick = { onNavigation(item.identifier) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = {
                            if (!item.actionFlag || navSuiteType != NavigationSuiteType.NavigationBar) {
                                Text(item.label)
                            }
                        }
                    )
                }
        }
    ){
        CompositionLocalProvider(
            LocalTopInfo provides TopInfo(visible)
        ) {
            content.invoke()
        }
    }
}