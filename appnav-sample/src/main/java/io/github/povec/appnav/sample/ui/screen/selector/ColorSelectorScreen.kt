package io.github.povec.appnav.sample.ui.screen.selector

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.povec.appnav.core.AppNavController
import io.github.povec.appnav.core.LocalNavController
import io.github.povec.appnav.sample.ui.data.RGBColor
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.component.appbar.LocalNavActionInfo
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.component.appbar.NavActionInfo
import io.github.povec.appnav.sample.ui.navigation.stage.component.toButton
import io.github.povec.appnav.sample.ui.screen.component.ColorItems

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorSelectorScreen(
    controller: AppNavController = LocalNavController.current,
    navActionInfo: NavActionInfo = LocalNavActionInfo.current
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pick a Color") },
                navigationIcon = {
                    navActionInfo.navIconType.toButton(onPop = controller::pop)
                }
            )
        }
    ) { innerPadding ->
        ColorItems(
            items = RGBColor.entries,
            modifier = Modifier.padding(innerPadding),
            onItemSelected = { item ->
                controller.send(ColorSelectorResult(item))
                controller.pop()
            }
        )
    }
}