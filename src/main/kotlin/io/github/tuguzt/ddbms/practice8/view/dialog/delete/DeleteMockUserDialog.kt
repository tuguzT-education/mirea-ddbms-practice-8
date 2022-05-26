package io.github.tuguzt.ddbms.practice8.view.dialog.delete

import androidx.compose.runtime.Composable

@Composable
fun DeleteMockUserDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) = DeleteDialog(
    title = "Delete mock user",
    onConfirm = onConfirm,
    onCancel = onCancel,
)
