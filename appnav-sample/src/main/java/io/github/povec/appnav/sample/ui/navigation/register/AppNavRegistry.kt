package io.github.povec.appnav.sample.ui.navigation.register

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ThumbUp
import io.github.povec.appnav.key.AppNavSession
import io.github.povec.appnav.registry.appNavRegistry
import io.github.povec.appnav.sample.ui.data.RGBColor
import io.github.povec.appnav.sample.ui.navigation.stage.component.NavLocation
import io.github.povec.appnav.sample.ui.navigation.stage.component.NavigationItem
import io.github.povec.appnav.sample.ui.screen.home.HomeArg
import io.github.povec.appnav.sample.ui.screen.viewer.ColorViewerArg

object ConstSession{
    val HOME = AppNavSession.Type.SPECIFIC - "home"
    val RED = AppNavSession.Type.SPECIFIC - "red"
    val GREEN = AppNavSession.Type.SPECIFIC - "green"
    val BLUE = AppNavSession.Type.SPECIFIC - "blue"
}

val appNavRegistry = appNavRegistry {

    register(
        identifier = ConstSession.HOME,
        arg = HomeArg,
    )

    register(
        identifier = ConstSession.RED,
        arg = ColorViewerArg(
            rgbColor = RGBColor.Red
        ),
    )

    register(
        identifier = ConstSession.GREEN,
        arg = ColorViewerArg(
            rgbColor = RGBColor.Green
        ),
    )

    register(
        identifier = ConstSession.BLUE,
        arg = ColorViewerArg(
            rgbColor = RGBColor.Blue
        ),
    )

}

val navigationItems = listOf(
    NavigationItem(
        identifier = ConstSession.HOME,
        label = "home",
        icon = Icons.Default.Home,
        locationFlag = NavLocation.FIRST,
        actionFlag = false,
    ),
    NavigationItem(
        identifier = ConstSession.RED,
        label = "red",
        icon = Icons.Default.Favorite,
        locationFlag = NavLocation.FIRST,
        actionFlag = false,
    ),
    NavigationItem(
        identifier = ConstSession.GREEN,
        label = "green",
        icon = Icons.Filled.ThumbUp,
        locationFlag = NavLocation.FIRST,
        actionFlag = false,
    ),
    NavigationItem(
        identifier = ConstSession.BLUE,
        label = "blue",
        icon = Icons.Filled.Build,
        locationFlag = NavLocation.FIRST,
        actionFlag = false,
    ),
)