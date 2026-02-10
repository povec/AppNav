package io.github.povec.appnav.sample.ui.screen.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.povec.appnav.core.AppNavController
import io.github.povec.appnav.core.LocalNavController
import io.github.povec.appnav.key.AppNavAction
import io.github.povec.appnav.key.AppNavConnect
import io.github.povec.appnav.sample.ui.data.RGBColor
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.component.appbar.LocalNavActionInfo
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.component.appbar.NavActionInfo
import io.github.povec.appnav.sample.ui.navigation.stage.component.toButton
import io.github.povec.appnav.sample.ui.screen.component.RGBSwitcher
import io.github.povec.appnav.sample.ui.screen.live.ColorLiveViewerArg
import io.github.povec.appnav.sample.ui.screen.selector.ColorSelectorArg
import io.github.povec.appnav.sample.ui.screen.selector.ColorSelectorMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorEditorScreen(
    controller: AppNavController = LocalNavController.current,
    navActionInfo: NavActionInfo = LocalNavActionInfo.current
) {
    var r by rememberSaveable { mutableStateOf(true) }
    var g by rememberSaveable { mutableStateOf(true) }
    var b by rememberSaveable { mutableStateOf(true) }

    val rgbColor by remember(r, g, b) {
        mutableStateOf(RGBColor.find(r, g, b) ?: RGBColor.White)
    }

    // 状態をリアルタイムに掲示（publish）
    LaunchedEffect(rgbColor) {
        controller.publish(rgbColor)
    }

    // 通知を待ち受け（receive）
    controller.Receive<ColorSelectorMessage> { (result) ->
        r = result.rgbColor.red
        g = result.rgbColor.green
        b = result.rgbColor.blue
        true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Color Editor",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = { navActionInfo.navIconType.toButton(onPop = controller::pop) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )
        }
    ) { innerPadding ->
        // スクロール可能なメインコンテンツ
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // セクション1: RGB操作パネル
            SectionWrapper(title = "Manual Adjustment") {
                RGBSwitcher(
                    red = rgbColor.red,
                    green = rgbColor.green,
                    blue = rgbColor.blue,
                    onRedChange = { r = it },
                    onGreenChange = { g = it },
                    onBlueChange = { b = it }
                )
            }

            // セクション2: プリセット選択
            SectionWrapper(title = "Presets") {
                OutlinedButton(
                    onClick = {
                        controller.navigate(ColorSelectorArg, AppNavAction.Expand(1), AppNavConnect())
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Icon(Icons.Default.Face, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Column(horizontalAlignment = Alignment.Start) {
                        Text("レシピから選ぶ", style = MaterialTheme.typography.titleMedium)
                        Text("定義済みの色から素早く選択", style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                }
            }

            // セクション3: アクション（下部に固定せず、スクロールの最後に配置）
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    controller.navigate(ColorLiveViewerArg, AppNavAction.Expand(), AppNavConnect())
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text("Live Viewer を起動", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

/**
 * デザインの統一感を出すためのセクション用ラッパー
 */
@Composable
private fun SectionWrapper(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp)
        )
        content()
    }
}