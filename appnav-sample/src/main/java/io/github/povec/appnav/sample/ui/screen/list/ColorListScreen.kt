package io.github.povec.appnav.sample.ui.screen.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.povec.appnav.core.AppNavController
import io.github.povec.appnav.core.LocalNavController
import io.github.povec.appnav.key.AppNavAction
import io.github.povec.appnav.sample.ui.data.RGBColor
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.component.appbar.LocalNavActionInfo
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.component.appbar.NavActionInfo
import io.github.povec.appnav.sample.ui.navigation.stage.component.toButton
import io.github.povec.appnav.sample.ui.screen.component.ColorItems
import io.github.povec.appnav.sample.ui.screen.filter.ColorFilterMessage
import io.github.povec.appnav.sample.ui.screen.viewer.ColorViewerArg

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ColorListScreen(
    controller: AppNavController = LocalNavController.current,
    navActionInfo: NavActionInfo = LocalNavActionInfo.current
) {

    // フィルターの状態管理
    var filterRed by rememberSaveable { mutableStateOf(true) }
    var filterGreen by rememberSaveable { mutableStateOf(true) }
    var filterBlue by rememberSaveable { mutableStateOf(true) }

    controller.Receive<ColorFilterMessage> { (result) ->
        filterRed = result.red
        filterGreen = result.green
        filterBlue = result.blue
        true
    }

    // フィルター条件に合致するアイテムを抽出
    val filteredList = rememberSaveable(filterRed, filterGreen, filterBlue) {
        RGBColor.entries.filter { item ->
            (filterRed && item.red) || (filterGreen && item.green) || (filterBlue && item.blue)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Color Explorer") },
                navigationIcon = {
                    navActionInfo.navIconType.toButton(onPop = controller::pop)
                }
            )
        }
    ) { innerPadding ->
        if (filteredList.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("No colors match these filters.", color = MaterialTheme.colorScheme.outline)
            }
        } else {
            ColorItems(
                items = filteredList,
                onItemSelected = { item -> controller.navigate(ColorViewerArg(item), AppNavAction.Expand()) },
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            )
        }
    }
}