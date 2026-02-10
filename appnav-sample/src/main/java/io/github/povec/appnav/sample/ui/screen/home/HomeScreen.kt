package io.github.povec.appnav.sample.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.github.povec.appnav.core.AppNavController
import io.github.povec.appnav.core.LocalNavController
import io.github.povec.appnav.sample.ui.data.RGBColor
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.component.appbar.LocalNavActionInfo
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.component.appbar.NavActionInfo
import io.github.povec.appnav.sample.ui.navigation.stage.component.toButton
import io.github.povec.appnav.sample.ui.screen.editor.ColorEditorArg
import io.github.povec.appnav.sample.ui.screen.list.ColorListArg
import io.github.povec.appnav.sample.ui.screen.viewer.ColorViewerArg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    controller: AppNavController = LocalNavController.current,
    navActionInfo: NavActionInfo = LocalNavActionInfo.current
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RGB Dashboard") },
                navigationIcon = {
                    navActionInfo.navIconType.toButton(onPop = controller::pop)
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- ヒーロービジュアル ---
            item {
                Spacer(Modifier.height(24.dp))
                RGBHeroVisual()
                Spacer(Modifier.height(24.dp))
            }

            // --- 特殊シナリオセクション ---
            item {
                Column(Modifier.padding(horizontal = 24.dp)) {
                    Text(
                        text = "Special Scenarios",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(Modifier.height(12.dp))
                    DemoCard(
                        title = "List-Detail-Extra",
                        description = "アダプティブなList-Detail-Extraのデモ",
                        icon = Icons.Default.Star,
                        onClick = { controller.startSession(ColorListArg) }
                    )
                    Spacer(Modifier.height(12.dp))
                    DemoCard(
                        title = "Selector-Editor-Viewer",
                        description = "アダプティブなSelector-Editor-Viewer構成のデモ",
                        icon = Icons.Default.Star,
                        onClick = { controller.startSession(ColorEditorArg) }
                    )
                }
                Spacer(Modifier.height(32.dp))
            }

            // --- Color Explorer セクション ---
            item {
                Text(
                    text = "Color Explorer",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }

            items(RGBColor.entries) { item ->
                ListItem(
                    headlineContent = {
                        Text(item.name, style = MaterialTheme.typography.titleMedium)
                    },
                    supportingContent = {
                        Text(item.description)
                    },
                    leadingContent = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = Color(item.r, item.g, item.b),
                                    shape = CircleShape
                                )
                        )
                    },
                    modifier = Modifier.clickable {
                        controller.startSession(ColorViewerArg(item))
                    }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun RGBHeroVisual() {
    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        val colors = listOf(Color.Red, Color.Green, Color.Blue)
        colors.forEachIndexed { index, color ->
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .offset(
                        x = if (index == 0) 0.dp else if (index == 1) (-30).dp else 30.dp,
                        y = if (index == 0) (-30).dp else 30.dp
                    )
                    .background(color.copy(alpha = 0.6f), CircleShape)
            )
        }
    }
}

@Composable
private fun DemoCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(40.dp))
            Spacer(Modifier.width(20.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleLarge)
                Text(description, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}