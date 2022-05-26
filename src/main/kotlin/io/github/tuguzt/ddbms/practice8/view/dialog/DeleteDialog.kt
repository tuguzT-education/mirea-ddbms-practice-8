package io.github.tuguzt.ddbms.practice8.view.dialog

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import io.github.tuguzt.ddbms.practice8.capitalize
import io.github.tuguzt.ddbms.practice8.view.theme.Practice8Theme

@Composable
fun DeleteDialog(
    title: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    val deleteTitle = "delete $title"
    Dialog(
        title = deleteTitle.capitalize(),
        onCloseRequest = onCancel,
        resizable = false,
        state = rememberDialogState(height = 250.dp),
    ) {
        Content(title = deleteTitle, onConfirm = onConfirm, onCancel = onCancel)
    }
}

@Composable
private fun Content(
    title: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    DialogSurface {
        Column(
            modifier = Modifier.matchParentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Are you sure you want to $title?",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.fillMaxWidth(fraction = 0.8f),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))

            ChoiceButtonRow(
                onClickCancel = onCancel,
                cancelTooltip = "Cancel deleting document",
                onClickConfirm = onConfirm,
                confirmTooltip = "Confirm deleting document",
                enabledConfirm = true,
                modifier = Modifier.weight(1f).fillMaxWidth(),
            )
        }
    }
}

@Composable
@Preview
private fun DeleteDialogPreview() {
    Practice8Theme {
        Content(onConfirm = {}, onCancel = {}, title = "Delete some data")
    }
}
