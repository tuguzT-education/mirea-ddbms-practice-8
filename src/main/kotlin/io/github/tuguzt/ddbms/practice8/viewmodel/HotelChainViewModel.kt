package io.github.tuguzt.ddbms.practice8.viewmodel

import io.github.tuguzt.ddbms.practice8.model.HotelChain
import kotlinx.coroutines.CoroutineScope
import org.litote.kmongo.coroutine.CoroutineCollection

class HotelChainViewModel(
    viewModelScope: CoroutineScope,
    private val collection: CoroutineCollection<HotelChain>,
) : ViewModel(viewModelScope) {
    suspend fun getAll(): List<HotelChain> = collection.find().toList()
}
