package io.github.tuguzt.ddbms.practice8.viewmodel

import io.github.tuguzt.ddbms.practice8.model.Client
import kotlinx.coroutines.CoroutineScope
import org.litote.kmongo.coroutine.CoroutineCollection

class ClientViewModel(
    viewModelScope: CoroutineScope,
    private val collection: CoroutineCollection<Client>,
) : ViewModel(viewModelScope) {
    suspend fun getAll(): List<Client> = collection.find().toList()
}
