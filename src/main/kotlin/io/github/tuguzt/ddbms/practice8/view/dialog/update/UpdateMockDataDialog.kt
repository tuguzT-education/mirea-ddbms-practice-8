package io.github.tuguzt.ddbms.practice8.view.dialog.update

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import io.github.tuguzt.ddbms.practice8.model.MockData
import io.github.tuguzt.ddbms.practice8.view.Tooltip
import io.github.tuguzt.ddbms.practice8.view.dialog.DialogSurface
import io.github.tuguzt.ddbms.practice8.view.dialog.OutlinedSingleLineTextField
import io.github.tuguzt.ddbms.practice8.view.theme.Practice8Theme

@Composable
fun UpdateMockDataDialog(
    onCloseRequest: () -> Unit,
    data: MockData,
    onUpdateData: (MockData) -> Unit,
) {
    Dialog(
        title = "Update mock data",
        state = rememberDialogState(height = 400.dp),
        onCloseRequest = onCloseRequest,
    ) {
        Content(onCloseRequest = onCloseRequest, data = data, onUpdateData = onUpdateData)
    }
}

@Composable
private fun Content(
    onCloseRequest: () -> Unit,
    data: MockData,
    onUpdateData: (MockData) -> Unit,
) {
    var data1 by remember { mutableStateOf("${data.data1}") }
    var data2 by remember { mutableStateOf(data.data2) }
    var data3 by remember { mutableStateOf("${data.data3}") }

    @Suppress("NAME_SHADOWING")
    val update = {
        val data = MockData(data1.toInt(), data2, data3.toLong())
        onUpdateData(data)
    }

    DialogSurface {
        Column(modifier = Modifier.matchParentSize()) {
            Tooltip(text = "Input field for \"data 1\"") {
                OutlinedSingleLineTextField(
                    text = "Data 1",
                    value = data1,
                    onValueChange = { data1 = it },
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Tooltip(text = "Input field for \"data 2\"") {
                OutlinedSingleLineTextField(
                    text = "Data 2",
                    value = data2,
                    onValueChange = { data2 = it },
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Tooltip(text = "Input field for \"data 3\"") {
                OutlinedSingleLineTextField(
                    text = "Data 3",
                    value = data3,
                    onValueChange = { data3 = it },
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            UpdateButtonRow(
                onClickCancel = onCloseRequest,
                onClickConfirm = update,
                enabledConfirm = data1.toIntOrNull() != data.data1
                        && data2 != data.data2
                        && data3.toLongOrNull() != data.data3,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
@Preview
private fun UpdateMockDataDialogPreview() {
    Practice8Theme {
        Content(
            onCloseRequest = {},
            data = MockData(data1 = -341, data2 = "eri", data3 = 54347),
            onUpdateData = {},
        )
    }
}
