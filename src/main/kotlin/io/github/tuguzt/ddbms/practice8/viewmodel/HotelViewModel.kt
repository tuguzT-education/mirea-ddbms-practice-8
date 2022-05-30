package io.github.tuguzt.ddbms.practice8.viewmodel

import io.github.tuguzt.ddbms.practice8.model.Hotel
import kotlinx.coroutines.CoroutineScope
import org.litote.kmongo.coroutine.CoroutineCollection

class HotelViewModel(
    viewModelScope: CoroutineScope,
    private val collection: CoroutineCollection<Hotel>,
) : ViewModel(viewModelScope) {
    suspend fun getAll(): List<Hotel> = collection.find().toList()
}
