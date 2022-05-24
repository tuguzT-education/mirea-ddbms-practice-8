package io.github.tuguzt.ddbms.practice8.viewmodel

import io.github.tuguzt.ddbms.practice8.model.MockData
import io.github.tuguzt.ddbms.practice8.model.MockUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.litote.kmongo.coroutine.CoroutineClient

class MainScreenViewModel(viewModelScope: CoroutineScope, client: CoroutineClient) : ViewModel(viewModelScope) {
    private val database = client.getDatabase("ddbms-practice-8")

    private val userCollection = database.getCollection<MockUser>()
    private val dataCollection = database.getCollection<MockData>()
    val collectionNames = listOf(
        requireNotNull(MockUser::class.simpleName),
        requireNotNull(MockData::class.simpleName),
    )

    private val _selectedCollectionName = MutableStateFlow(collectionNames.first())
    val selectedCollectionName = _selectedCollectionName.asStateFlow()

    suspend fun selectCollection(name: String) {
        if (name !in collectionNames) throw IllegalArgumentException("Wrong collection name")
        _selectedCollectionName.emit(name)
        updateTableRows()
    }

    private val userCollectionFieldNames = listOf(MockUser::name.name, MockUser::age.name)
    private val dataCollectionFieldNames = listOf(MockData::data1.name, MockData::data2.name, MockData::data3.name)

    private var _fieldNames = MutableStateFlow(userCollectionFieldNames)
    val fieldNames = _fieldNames.asStateFlow()

    private var _tableRows = MutableStateFlow(listOf<List<String>>())
    val tableRows = _tableRows.asStateFlow()

    private suspend fun updateTableRows() {
        when (selectedCollectionName.value) {
            MockUser::class.simpleName -> {
                val tableRows = userCollection.find().toList().map { listOf(it.name, "${it.age}") }
                _tableRows.emit(tableRows)
                _fieldNames.emit(userCollectionFieldNames)
            }
            MockData::class.simpleName -> {
                val tableRows = dataCollection.find().toList().map { listOf("${it.data1}", it.data2, "${it.data3}") }
                _tableRows.emit(tableRows)
                _fieldNames.emit(dataCollectionFieldNames)
            }
        }
    }

    suspend fun insertUser(user: MockUser) {
        userCollection.insertOne(user)
        updateTableRows()
    }

    suspend fun insertData(data: MockData) {
        dataCollection.insertOne(data)
        updateTableRows()
    }
}
