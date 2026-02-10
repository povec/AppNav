package io.github.povec.appnav.sample.ui.screen.component

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.povec.appnav.sample.ui.data.RGBColor

@Composable
fun ColorViewer(
    modifier: Modifier = Modifier,
    rgbColor: RGBColor.Item
) {
    var sliderValue by rememberSaveable { mutableStateOf(1f) }

    // ベースとなる色
    val baseColor = Color(rgbColor.r, rgbColor.g, rgbColor.b)
    // スライダーで変化した後の最終的な表示色
    val activeColor = Color(
        rgbColor.r * sliderValue,
        rgbColor.g * sliderValue,
        rgbColor.b * sliderValue
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ヘッダー部分：担当色のバッジ
        Surface(
            color = baseColor.copy(alpha = 0.1f),
            shape = RoundedCornerShape(50),
            border = BorderStroke(1.dp, baseColor.copy(alpha = 0.3f))
        ) {
            Text(
                text = "${rgbColor.name.uppercase()} CHANNEL",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelLarge,
                color = baseColor,
                letterSpacing = 2.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // メインプレビュー：光り輝くような演出
        Box(contentAlignment = Alignment.Center) {
            // 背後のぼかし（発光エフェクト）
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .graphicsLayer {
                        alpha = 0.4f
                        renderEffect = BlurEffect(80f, 80f) // API 31+
                    }
                    .background(activeColor, CircleShape)
            )

            // メインの色の塊
            Surface(
                modifier = Modifier.size(180.dp),
                color = activeColor,
                shape = RoundedCornerShape(32.dp),
                tonalElevation = 8.dp,
                shadowElevation = 12.dp
            ) {
                // 内側に現在の値を薄く表示
                Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "${(sliderValue * 100).toInt()}%",
                        color = if ((rgbColor.r + rgbColor.g + rgbColor.b) * sliderValue > 1.5f)
                            Color.Black.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.3f),
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(64.dp))

        // スライダーセクション
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Intensity",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${(sliderValue * 255).toInt()}",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium),
                    color = baseColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                colors = SliderDefaults.colors(
                    thumbColor = baseColor,
                    activeTrackColor = baseColor,
                    inactiveTrackColor = baseColor.copy(alpha = 0.2f)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Text("255", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}