package io.github.tuguzt.ddbms.practice8.view.dialog.delete

import androidx.compose.runtime.Composable

@Composable
fun DeleteMockDataDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) = DeleteDialog(
    title = "Delete mock data",
    onConfirm = onConfirm,
    onCancel = onCancel,
)
