package io.github.tuguzt.ddbms.practice8.viewmodel

import io.github.tuguzt.ddbms.practice8.model.Room
import kotlinx.coroutines.CoroutineScope
import org.litote.kmongo.coroutine.CoroutineCollection

class RoomViewModel(
    viewModelScope: CoroutineScope,
    private val collection: CoroutineCollection<Room>,
) : ViewModel(viewModelScope) {
    suspend fun getAll(): List<Room> = collection.find().toList()
}
