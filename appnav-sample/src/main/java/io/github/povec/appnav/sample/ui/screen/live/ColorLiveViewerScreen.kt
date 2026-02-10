package io.github.povec.appnav.sample.ui.screen.live

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.povec.appnav.core.AppNavController
import io.github.povec.appnav.core.LocalNavController
import io.github.povec.appnav.sample.ui.data.RGBColor
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.component.appbar.LocalNavActionInfo
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.component.appbar.NavActionInfo
import io.github.povec.appnav.sample.ui.navigation.stage.component.toButton
import io.github.povec.appnav.sample.ui.screen.component.ColorViewer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorLiveViewerScreen(
    controller: AppNavController = LocalNavController.current,
    navActionInfo: NavActionInfo = LocalNavActionInfo.current,
) {

    var r by rememberSaveable { mutableStateOf(true) }
    var g by rememberSaveable { mutableStateOf(true) }
    var b by rememberSaveable { mutableStateOf(true) }

    controller.Subscribe<RGBColor.Item> {
        r = it.red
        g = it.green
        b = it.blue
    }

    var rgbColor by remember(r, g, b) { mutableStateOf(RGBColor.find(r, g, b)?: RGBColor.White) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(rgbColor.name) },
                navigationIcon = {
                    navActionInfo.navIconType.toButton(onPop = controller::pop)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()) // スクロールを有効化
                .padding(vertical = 24.dp) // 上下のゆとり
        ) {
            ColorViewer(
                modifier = Modifier.fillMaxWidth(),
                rgbColor = rgbColor
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}