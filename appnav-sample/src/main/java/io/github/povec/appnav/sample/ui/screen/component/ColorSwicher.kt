package io.github.povec.appnav.sample.ui.screen.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RGBSwitcher(
    red: Boolean,
    green: Boolean,
    blue: Boolean,
    onRedChange: (Boolean) -> Unit,
    onGreenChange: (Boolean) -> Unit,
    onBlueChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Color Channels",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                RGBSwitchItem(
                    label = "Red",
                    checked = red,
                    activeColor = Color(0xFFEF5350), // Soft Red
                    onCheckedChange = onRedChange
                )
                RGBSwitchItem(
                    label = "Green",
                    checked = green,
                    activeColor = Color(0xFF66BB6A), // Soft Green
                    onCheckedChange = onGreenChange
                )
                RGBSwitchItem(
                    label = "Blue",
                    checked = blue,
                    activeColor = Color(0xFF42A5F5), // Soft Blue
                    onCheckedChange = onBlueChange
                )
            }
        }
    }
}

@Composable
private fun RGBSwitchItem(
    label: String,
    checked: Boolean,
    activeColor: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (checked) activeColor.copy(alpha = 0.1f) else Color.Transparent)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // 色のプレビュー用ドット
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = if (checked) activeColor else activeColor.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = if (checked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = activeColor,
                checkedTrackColor = activeColor.copy(alpha = 0.3f)
            )
        )
    }
}