package io.github.tuguzt.ddbms.practice8.viewmodel

import io.github.tuguzt.ddbms.practice8.model.ProductCategory
import kotlinx.coroutines.CoroutineScope
import org.litote.kmongo.coroutine.CoroutineCollection

class ProductCategoryViewModel(
    viewModelScope: CoroutineScope,
    private val collection: CoroutineCollection<ProductCategory>,
) : ViewModel(viewModelScope) {
    suspend fun getAll(): List<ProductCategory> = collection.find().toList()
}
