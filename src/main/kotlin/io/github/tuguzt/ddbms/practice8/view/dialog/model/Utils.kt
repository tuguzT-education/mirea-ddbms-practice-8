package io.github.tuguzt.ddbms.practice8.view.dialog.model

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.tuguzt.ddbms.practice8.view.dialog.ChoiceButtonRow
import io.github.tuguzt.ddbms.practice8.view.dialog.DialogSurface

// todo "create Content @Composable for each table"
@Composable
fun TextFieldContainer(
    actionText: String,
    enabledConfirm: Boolean,
    onCloseRequest: () -> Unit,
    onClickConfirm: () -> Unit,
    content: @Composable () -> Unit,
) = DialogSurface {
    Column(modifier = Modifier.matchParentSize()) {
        content()
        ChoiceButtonRow(
            onClickCancel = onCloseRequest,
            cancelTooltip = "Cancel ${actionText}ing document",
            onClickConfirm = onClickConfirm,
            confirmTooltip = "Confirm ${actionText}ing document",
            enabledConfirm = enabledConfirm,
            modifier = Modifier.weight(1f),
        )
    }
}
