package io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.component.dialog

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.povec.appnav.sample.ui.navigation.scene.constraint.common.metadata.DialogBehavior

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogContainer(
    dialogBehavior: DialogBehavior?,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val behavior = dialogBehavior ?: DialogBehavior.DEFAULT

    when (behavior) {

        DialogBehavior.BottomSheet -> {
            ModalBottomSheet(
                onDismissRequest = onDismiss,
            ) {
                content()
            }
        }

        DialogBehavior.Overlay -> {
            Dialog(
                onDismissRequest = onDismiss,
                properties = DialogProperties(
                    usePlatformDefaultWidth = false
                )
            ) {
                content()
            }
        }
    }
}