package io.github.tuguzt.ddbms.practice8.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.tuguzt.ddbms.practice8.model.Mock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.litote.kmongo.coroutine.CoroutineDatabase

@Composable
fun MainScreen(
    database: CoroutineDatabase,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    val collection = remember { database.getCollection<Mock>("mock") }
    var list by remember { mutableStateOf(listOf<Mock>()) }

    suspend fun update() {
        list = collection.find().toList()
    }

    LaunchedEffect(Unit) { update() }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        collection.insertOne(Mock(name = "Hello World"))
                        update()
                    }
                }
            ) {
                Text(text = "Add new mock")
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(contentPadding = PaddingValues(16.dp)) {
                itemsIndexed(items = list) { index, mock ->
                    Text(text = "${mock.name} $index")
                }
            }
        }
    }
}
