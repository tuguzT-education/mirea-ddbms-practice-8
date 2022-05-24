package io.github.tuguzt.ddbms.practice8.viewmodel

import io.github.tuguzt.ddbms.practice8.escapeRegex
import io.github.tuguzt.ddbms.practice8.model.MockData
import io.github.tuguzt.ddbms.practice8.model.MockUser
import io.github.tuguzt.ddbms.practice8.regex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.regex
import org.litote.kmongo.textIndex

class MainScreenViewModel(viewModelScope: CoroutineScope, client: CoroutineClient) : ViewModel(viewModelScope) {
    private val database = client.getDatabase("ddbms-practice-8")

    private val userCollection = database.getCollection<MockUser>()
    private val dataCollection = database.getCollection<MockData>()

    init {
        viewModelScope.launch {
            userCollection.createIndex(MockUser::name.textIndex())
            userCollection.createIndex(MockUser::age.textIndex())

            dataCollection.createIndex(MockData::data1.textIndex())
            dataCollection.createIndex(MockData::data2.textIndex())
            dataCollection.createIndex(MockData::data3.textIndex())
        }
    }

    val collectionNames = listOf(
        requireNotNull(MockUser::class.simpleName),
        requireNotNull(MockData::class.simpleName),
    )

    private val _selectedCollectionName = MutableStateFlow(collectionNames.first())
    val selectedCollectionName = _selectedCollectionName.asStateFlow()

    private val userCollectionFieldNames = listOf(MockUser::name.name, MockUser::age.name)
    private val dataCollectionFieldNames = listOf(MockData::data1.name, MockData::data2.name, MockData::data3.name)

    private val _selectedFieldName = MutableStateFlow(userCollectionFieldNames.first())

    private var _fieldNames = MutableStateFlow(userCollectionFieldNames)
    val fieldNames = _fieldNames.asStateFlow()

    private var _tableRows = MutableStateFlow(listOf<List<String>>())
    val tableRows = _tableRows.asStateFlow()

    private suspend fun updateTableRows() {
        when (selectedCollectionName.value) {
            MockUser::class.simpleName -> {
                val tableRows = userCollection.find().toList().map { it.toTableRow() }
                _tableRows.emit(tableRows)
                _fieldNames.emit(userCollectionFieldNames)
            }
            MockData::class.simpleName -> {
                val tableRows = dataCollection.find().toList().map { it.toTableRow() }
                _tableRows.emit(tableRows)
                _fieldNames.emit(dataCollectionFieldNames)
            }
            else -> throw IllegalArgumentException("Wrong selected collection name")
        }
    }

    suspend fun selectCollection(name: String) {
        if (name !in collectionNames) throw IllegalArgumentException("Wrong collection name")
        _selectedCollectionName.emit(name)
        updateTableRows()
    }

    suspend fun selectField(name: String) {
        when (name) {
            in userCollectionFieldNames -> _selectedFieldName.emit(value = name)
            in dataCollectionFieldNames -> _selectedFieldName.emit(value = name)
            else -> throw IllegalArgumentException("Wrong field name")
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

    suspend fun search(searchText: String) {
        if (searchText.isBlank()) {
            updateTableRows()
            return
        }

        val escapedSearchText = searchText.trim().asSequence().map(Char::escapeRegex).joinToString(separator = "")
        val regex = ".*$escapedSearchText.*"
        val tableRows = when (selectedCollectionName.value) {
            MockUser::class.simpleName -> {
                val filter = when (_selectedFieldName.value) {
                    MockUser::name.name -> MockUser::name regex regex
                    MockUser::age.name -> MockUser::age regex regex
                    else -> throw IllegalStateException("Wrong selected field name")
                }
                userCollection.find(filter).toList().map { it.toTableRow() }
            }
            MockData::class.simpleName -> {
                val filter = when (_selectedFieldName.value) {
                    MockData::data1.name -> MockData::data1 regex regex
                    MockData::data2.name -> MockData::data2 regex regex
                    MockData::data3.name -> MockData::data3 regex regex
                    else -> throw IllegalStateException("Wrong selected field name")
                }
                dataCollection.find(filter).toList().map { it.toTableRow() }
            }
            else -> throw IllegalStateException("Wrong selected collection name")
        }
        _tableRows.emit(tableRows)
    }
}

private fun MockUser.toTableRow(): List<String> = listOf(name, "$age")

private fun MockData.toTableRow(): List<String> = listOf("$data1", data2, "$data3")
