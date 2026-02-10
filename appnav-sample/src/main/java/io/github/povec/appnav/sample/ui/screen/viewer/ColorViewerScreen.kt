package io.github.povec.appnav.sample.ui.screen.viewer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.povec.appnav.core.AppNavController
import io.github.povec.appnav.core.LocalNavController
import io.github.povec.appnav.key.AppNavAction
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.component.appbar.LocalNavActionInfo
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.component.appbar.NavActionInfo
import io.github.povec.appnav.sample.ui.navigation.stage.component.toButton
import io.github.povec.appnav.sample.ui.screen.component.ColorViewer
import io.github.povec.appnav.sample.ui.screen.extra.ColorExtraArg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorViewerScreen(
    controller: AppNavController = LocalNavController.current,
    navActionInfo: NavActionInfo = LocalNavActionInfo.current,
    arg: ColorViewerArg,
){
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(arg.rgbColor.name) },
                navigationIcon = {
                    navActionInfo.navIconType.toButton( onPop = controller::pop )
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 1.dp
            ) {
                OutlinedButton(
                    onClick = {
                        controller.navigate(ColorExtraArg(rgbColor = arg.rgbColor), AppNavAction.Expand())
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("詳しく")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()) // スクロールを有効化
        ) {
            // 1. メインのカラー表示 (既存のコンポーネント)
            // 画面上部の大部分を占めるように weight を設定
            ColorViewer(
                modifier = Modifier,
                rgbColor = arg.rgbColor
            )
        }
    }
}