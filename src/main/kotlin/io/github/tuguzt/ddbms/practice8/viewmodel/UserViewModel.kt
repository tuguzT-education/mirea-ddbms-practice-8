package io.github.tuguzt.ddbms.practice8.viewmodel

import io.github.tuguzt.ddbms.practice8.model.User
import kotlinx.coroutines.CoroutineScope
import org.litote.kmongo.coroutine.CoroutineCollection

class UserViewModel(
    viewModelScope: CoroutineScope,
    private val collection: CoroutineCollection<User>,
) : ViewModel(viewModelScope) {
    suspend fun getAll(): List<User> = collection.find().toList()
}
