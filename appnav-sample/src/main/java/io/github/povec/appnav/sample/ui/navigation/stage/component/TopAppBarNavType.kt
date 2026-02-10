package io.github.povec.appnav.sample.ui.navigation.stage.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

enum class TopAppBarNavType{
    NONE,BACK, CLOSE
}

@Composable
fun TopAppBarNavType.toButton(
    modifier: Modifier = Modifier,
    onPop: () -> Unit,
) {
    when (this) {
        TopAppBarNavType.NONE -> {}
        TopAppBarNavType.BACK -> {
            IconButton(onClick = onPop, modifier = modifier) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }

        TopAppBarNavType.CLOSE -> {
            IconButton(onClick = onPop, modifier = modifier) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close"
                )
            }
        }
    }
}