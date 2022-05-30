package io.github.tuguzt.ddbms.practice8.viewmodel

import io.github.tuguzt.ddbms.practice8.model.Booking
import kotlinx.coroutines.CoroutineScope
import org.litote.kmongo.coroutine.CoroutineCollection

class BookingViewModel(
    viewModelScope: CoroutineScope,
    private val collection: CoroutineCollection<Booking>,
) : ViewModel(viewModelScope) {
    suspend fun getAll(): List<Booking> = collection.find().toList()
}
