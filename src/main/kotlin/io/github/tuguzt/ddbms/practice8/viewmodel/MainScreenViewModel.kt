package io.github.tuguzt.ddbms.practice8.viewmodel

import io.github.tuguzt.ddbms.practice8.docker.identifier
import io.github.tuguzt.ddbms.practice8.escapeRegex
import io.github.tuguzt.ddbms.practice8.model.MockData
import io.github.tuguzt.ddbms.practice8.model.MockUser
import io.github.tuguzt.ddbms.practice8.regex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.updateOne
import org.litote.kmongo.regex
import org.litote.kmongo.textIndex
import kotlin.reflect.KProperty

class MainScreenViewModel(viewModelScope: CoroutineScope, client: CoroutineClient) : ViewModel(viewModelScope) {
    private val database = client.getDatabase(identifier)

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

    private val userCollectionFieldSortOrders = linkedMapOf<KProperty<*>, Boolean>()
    private val userCollectionFields =
        listOf(MockUser::name.name, MockUser::age.name)

    private val dataCollectionFieldSortOrders = linkedMapOf<KProperty<*>, Boolean>()
    private val dataCollectionFields =
        listOf(MockData::data1.name, MockData::data2.name, MockData::data3.name)

    private val _selectedFieldName = MutableStateFlow(userCollectionFields.first())

    private var _fieldNames = MutableStateFlow(userCollectionFields)
    val fieldNames = _fieldNames.asStateFlow()

    private var _tableRows = MutableStateFlow(listOf<List<String>>())
    val tableRows = _tableRows.asStateFlow()

    private suspend fun updateTableRows() {
        suspend fun subUpdate(
            collection: CoroutineCollection<*>,
            collectionFields: List<String>,
            collectionFieldSortOrders: LinkedHashMap<KProperty<*>, Boolean>,
        ) {
            _tableRows.emit(combineFindSort(collection, collectionFieldSortOrders))
            _fieldNames.emit(collectionFields)
        }

        when (selectedCollectionName.value) {
            MockUser::class.simpleName ->
                subUpdate(userCollection, userCollectionFields, userCollectionFieldSortOrders)
            MockData::class.simpleName ->
                subUpdate(dataCollection, dataCollectionFields, dataCollectionFieldSortOrders)
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
            in userCollectionFields -> _selectedFieldName.emit(value = name)
            in dataCollectionFields -> _selectedFieldName.emit(value = name)
            else -> throw IllegalArgumentException("Wrong field name")
        }
    }

    suspend fun insertUser(user: MockUser) {
        userCollection.save(user)
        updateTableRows()
    }

    suspend fun updateUser(user: MockUser) {
        userCollection.updateOne(user)
        updateTableRows()
    }

    suspend fun deleteUser(user: MockUser) {
        val id = requireNotNull(user.id)
        userCollection.deleteOneById(id)
        updateTableRows()
    }

    suspend fun insertData(data: MockData) {
        dataCollection.save(data)
        updateTableRows()
    }

    suspend fun updateData(data: MockData) {
        dataCollection.updateOne(data)
        updateTableRows()
    }

    suspend fun deleteData(data: MockData) {
        val id = requireNotNull(data.id)
        dataCollection.deleteOneById(id)
        updateTableRows()
    }

    suspend fun sortByField(fieldName: String, searchText: String) {
        when (selectedCollectionName.value) {
            MockUser::class.simpleName -> manageFieldSortOrder(
                fieldName = fieldName,
                fieldSortOrders = userCollectionFieldSortOrders,
                property = when (fieldName) {
                    MockUser::name.name -> MockUser::name
                    MockUser::age.name -> MockUser::age
                    else -> throw IllegalArgumentException("Wrong field name passed.")
                },
            )
            MockData::class.simpleName -> manageFieldSortOrder(
                fieldName = fieldName,
                fieldSortOrders = dataCollectionFieldSortOrders,
                property = when (fieldName) {
                    MockData::data1.name -> MockData::data1
                    MockData::data2.name -> MockData::data2
                    MockData::data3.name -> MockData::data3
                    else -> throw IllegalArgumentException("Wrong field name passed.")
                }
            )
        }
        search(searchText)
    }

    suspend fun search(searchText: String) {
        if (searchText.isNotBlank()) {
            val regex = ".*${searchText.trim().asSequence().map(Char::escapeRegex).joinToString(separator = "")}.*"

            _tableRows.emit(
                when (selectedCollectionName.value) {
                    MockUser::class.simpleName -> combineFindSort(
                        userCollection,
                        userCollectionFieldSortOrders,
                        when (_selectedFieldName.value) {
                            MockUser::name.name -> MockUser::name regex regex
                            MockUser::age.name -> MockUser::age regex regex
                            else -> throw IllegalStateException("Wrong selected field name")
                        }
                    )
                    MockData::class.simpleName -> combineFindSort(
                        dataCollection,
                        dataCollectionFieldSortOrders,
                        when (_selectedFieldName.value) {
                            MockData::data1.name -> MockData::data1 regex regex
                            MockData::data2.name -> MockData::data2 regex regex
                            MockData::data3.name -> MockData::data3 regex regex
                            else -> throw IllegalStateException("Wrong selected field name")
                        }
                    )
                    else -> throw IllegalStateException("Wrong selected collection name")
                }
            )
        } else updateTableRows()
    }
}
