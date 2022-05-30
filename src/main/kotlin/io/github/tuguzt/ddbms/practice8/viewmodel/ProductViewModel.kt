package io.github.tuguzt.ddbms.practice8.viewmodel

import io.github.tuguzt.ddbms.practice8.model.Product
import kotlinx.coroutines.CoroutineScope
import org.litote.kmongo.coroutine.CoroutineCollection

class ProductViewModel(
    viewModelScope: CoroutineScope,
    private val collection: CoroutineCollection<Product>,
) : ViewModel(viewModelScope) {
    suspend fun getAll(): List<Product> = collection.find().toList()
}
