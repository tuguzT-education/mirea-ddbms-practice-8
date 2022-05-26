package io.github.tuguzt.ddbms.practice8.view.dialog.model

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.model.Identifiable
import io.github.tuguzt.ddbms.practice8.model.MockData
import io.github.tuguzt.ddbms.practice8.view.Tooltip
import io.github.tuguzt.ddbms.practice8.view.dialog.OutlinedSingleLineTextField

@Composable
fun MockDataContent(
    actionText: String,
    onCloseRequest: () -> Unit,
    onApplyToData: (Identifiable<*>) -> Unit,
    data: MockData? = null,
) {
    var data1 by remember { mutableStateOf(if (data != null) "${data.data1}" else "") }
    var data2 by remember { mutableStateOf(data?.data2 ?: "") }
    var data3 by remember { mutableStateOf(if (data != null) "${data.data3}" else "") }

    val onClickConfirm = {
        if (data == null) {
            onCloseRequest()
            onApplyToData(MockData(data1.toInt(), data2, data3.toLong()))
        } else onApplyToData(MockData(data1.toInt(), data2, data3.toLong(), data.id))
    }

    @Suppress("NAME_SHADOWING")
    val enabledConfirm =
        if (data == null)
            data1.toIntOrNull() != null && data2.isNotBlank() && data3.toLongOrNull() != null
        else run {
            val data1 = data1.toIntOrNull() ?: return@run false
            val data3 = data3.toLongOrNull() ?: return@run false
            data1 != data.data1
                    || data2 != data.data2
                    || data3 != data.data3
        }

    TextFieldContainer(
        actionText = actionText,
        enabledConfirm = enabledConfirm,
        onCloseRequest = onCloseRequest,
        onClickConfirm = onClickConfirm,
    ) {
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
    }
}
