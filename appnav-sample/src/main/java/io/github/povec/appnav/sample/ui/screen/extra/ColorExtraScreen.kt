package io.github.povec.appnav.sample.ui.screen.extra

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.povec.appnav.core.AppNavController
import io.github.povec.appnav.core.LocalNavController
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.component.appbar.LocalNavActionInfo
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.component.appbar.NavActionInfo
import io.github.povec.appnav.sample.ui.navigation.stage.component.toButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorExtraScreen(
    controller: AppNavController = LocalNavController.current,
    navActionInfo: NavActionInfo = LocalNavActionInfo.current,
    arg: ColorExtraArg,
) {

    val colorItem = arg.rgbColor

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(colorItem.name) },
                navigationIcon = {
                    navActionInfo.navIconType.toButton(onPop = controller::pop)
                },
                // 背景を透明にして、コンテンツと一体化させる
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. ダイナミックなカラーヘッダー
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(
                        color = Color(colorItem.r, colorItem.g, colorItem.b)
                    ),
                contentAlignment = Alignment.BottomStart
            ) {
                // 色名を目立たせる（コントラストを考慮）
                Text(
                    text = colorItem.name,
                    style = MaterialTheme.typography.displayLarge,
                    color = if (colorItem.red && colorItem.green) Color.Black else Color.White,
                    modifier = Modifier.padding(24.dp)
                )
            }

            // 2. 説明セクション
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = colorItem.description,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.height(40.dp))

                // 3. RGB成分の解析（Booleanデータを視覚化）
                Text(
                    text = "RGB Components",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ComponentTag(label = "Red", isActive = colorItem.red, color = Color.Red)
                    ComponentTag(label = "Green", isActive = colorItem.green, color = Color.Green)
                    ComponentTag(label = "Blue", isActive = colorItem.blue, color = Color.Blue)
                }
            }
        }
    }
}

@Composable
private fun ComponentTag(label: String, isActive: Boolean, color: Color) {
    Surface(
        shape = CircleShape,
        color = if (isActive) color.copy(alpha = 0.1f) else Color.Transparent,
        border = BorderStroke(
            width = 1.dp,
            color = if (isActive) color else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = if (isActive) color else MaterialTheme.colorScheme.outline
        )
    }
}