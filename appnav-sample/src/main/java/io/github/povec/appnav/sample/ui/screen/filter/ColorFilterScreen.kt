package io.github.povec.appnav.sample.ui.screen.filter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.povec.appnav.core.AppNavController
import io.github.povec.appnav.core.LocalNavController
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.component.appbar.LocalNavActionInfo
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.component.appbar.NavActionInfo
import io.github.povec.appnav.sample.ui.navigation.stage.component.toButton
import io.github.povec.appnav.sample.ui.screen.component.RGBSwitcher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorFilterScreen(
    controller: AppNavController = LocalNavController.current,
    navActionInfo: NavActionInfo = LocalNavActionInfo.current
) {
    var filterRed by rememberSaveable { mutableStateOf(false) }
    var filterGreen by rememberSaveable { mutableStateOf(false) }
    var filterBlue by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Filter Settings") },
                navigationIcon = {
                    navActionInfo.navIconType.toButton(onPop = controller::pop)
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // スイッチコンポーネント
            RGBSwitcher(
                red = filterRed,
                green = filterGreen,
                blue = filterBlue,
                onRedChange = { filterRed = it },
                onGreenChange = { filterGreen = it },
                onBlueChange = { filterBlue = it }
            )

            Button(
                onClick = {
                    controller.send(
                        ColorFilterResult(
                            red = filterRed,
                            green = filterGreen,
                            blue = filterBlue
                        )
                    )
                    controller.pop()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("決定")
            }

        }
    }
}