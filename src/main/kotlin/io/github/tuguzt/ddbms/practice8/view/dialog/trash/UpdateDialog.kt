package io.github.tuguzt.ddbms.practice8.view.dialog.trash

//import androidx.compose.desktop.ui.tooling.preview.Preview
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.height
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.window.Dialog
//import io.github.tuguzt.ddbms.practice8.capitalize
//import io.github.tuguzt.ddbms.practice8.model.Identifiable
//import io.github.tuguzt.ddbms.practice8.model.MockData
//import io.github.tuguzt.ddbms.practice8.model.MockUser
//import io.github.tuguzt.ddbms.practice8.view.Tooltip
//import io.github.tuguzt.ddbms.practice8.view.dialog.ChoiceButtonRow
//import io.github.tuguzt.ddbms.practice8.view.dialog.DialogSurface
//import io.github.tuguzt.ddbms.practice8.view.dialog.OutlinedSingleLineTextField
//import io.github.tuguzt.ddbms.practice8.view.theme.Practice8Theme
//import kotlin.reflect.KMutableProperty
//import kotlin.reflect.full.memberProperties
//import kotlin.reflect.jvm.isAccessible

//@Composable
//fun UpdateDialog(
//    item: Identifiable<*>,
//    onCloseRequest: () -> Unit,
//    onUpdateItem: (Identifiable<*>) -> Unit,
//) {
//    Dialog(
//        title = "Update ${item::class.simpleName}",
//        onCloseRequest = onCloseRequest,
//    ) {
//        Content(onCloseRequest = onCloseRequest, item = item, onUpdateItem = onUpdateItem)
//    }
//}
//
//@Composable
//private fun Content(
//    item: Identifiable<*>,
//    onCloseRequest: () -> Unit,
//    onUpdateItem: (Identifiable<*>) -> Unit,
//) {
//    val parameters = remember { linkedMapOf<String, KMutableProperty<*>>() }
//
//    item::class.memberProperties.forEach { member ->
//        member.isAccessible = true
//        if (member.name != "id") parameters[member.name] = member as KMutableProperty<*>
//    }
//
//    val iterator = parameters.iterator()
//    val data = when (item) {
//        is MockUser -> MockUser(
//            iterator.next().value.getter.call(item).toString(),
//            iterator.next().value.getter.call(item).toString().toInt(),
//            item.id)
//        is MockData -> MockData(
//            iterator.next().value.getter.call(item).toString().toInt(),
//            iterator.next().value.getter.call(item).toString(),
//            iterator.next().value.getter.call(item).toString().toLong(),
//            item.id)
//    }
//
//    val update = {
////        val iterator = parameters.iterator()
////        val data = when (item) {
////            is MockUser -> MockUser(
////                iterator.next().value.name,
////                iterator.next().value.name.toInt(),
////                item.id)
////            is MockData -> MockData(
////                iterator.next().value.name.toInt(),
////                iterator.next().value.name,
////                iterator.next().value.name.toLong(),
////                item.id)
////        }
//        onUpdateItem(data)
//    }
//
//    DialogSurface {
//        Column(modifier = Modifier.matchParentSize()) {
//
//
//            data::class.memberProperties.forEach { property ->
//                Tooltip(text = "Input field for \"${property.name.capitalize()}\"") {
//                    OutlinedSingleLineTextField(
//                        text = property.name.capitalize(),
//                        value = (property as KMutableProperty<*>).getter.call(data).toString(),
//                        onValueChange = { (property as KMutableProperty<*>).setter.call(data, it) },
//                    )
//                }
//                Spacer(modifier = Modifier.height(16.dp))
//            }
//            ChoiceButtonRow(
//                onClickCancel = onCloseRequest,
//                cancelTooltip = "Cancel updating document",
//                onClickConfirm = update,
//                confirmTooltip = "Confirm updating document",
//                enabledConfirm = true,
//                modifier = Modifier.weight(1f),
//            )
//        }
//    }
//}
//
//@Composable
//@Preview
//private fun UpdateDialogPreview() {
//    Practice8Theme {
//        Content(
//            onCloseRequest = {  },
//            item = MockData(data1 = -341, data2 = "eri", data3 = 54347),
//            onUpdateItem = {  },
//        )
//    }
//}
