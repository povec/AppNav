package io.github.povec.appnav.sample.ui.screen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.povec.appnav.sample.ui.data.RGBColor

@Composable
fun ColorItems(
    items: List<RGBColor.Item>,
    onItemSelected: (RGBColor.Item) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(items) { item ->
            ListItem(
                headlineContent = {
                    Text(item.name, style = MaterialTheme.typography.titleMedium)
                },
                supportingContent = {
                    Text(item.description)
                },
                leadingContent = {
                    // 色のプレビュー（円形）
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color(item.r, item.g, item.b),
                                shape = CircleShape
                            )
                    )
                },
                modifier = Modifier.clickable { onItemSelected(item) }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}