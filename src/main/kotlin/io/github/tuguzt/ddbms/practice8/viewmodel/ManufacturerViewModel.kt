package io.github.tuguzt.ddbms.practice8.viewmodel

import io.github.tuguzt.ddbms.practice8.model.Manufacturer
import kotlinx.coroutines.CoroutineScope
import org.litote.kmongo.coroutine.CoroutineCollection

class ManufacturerViewModel(
    viewModelScope: CoroutineScope,
    private val collection: CoroutineCollection<Manufacturer>,
) : ViewModel(viewModelScope) {
    suspend fun getAll(): List<Manufacturer> = collection.find().toList()
}
