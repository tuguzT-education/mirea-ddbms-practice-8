package io.github.tuguzt.ddbms.practice8.viewmodel

import io.github.tuguzt.ddbms.practice8.docker.identifier
import io.github.tuguzt.ddbms.practice8.escapeRegex
import io.github.tuguzt.ddbms.practice8.model.Identifiable
import io.github.tuguzt.ddbms.practice8.model.MockData
import io.github.tuguzt.ddbms.practice8.model.MockUser
import io.github.tuguzt.ddbms.practice8.regex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.updateOne
import org.litote.kmongo.regex
import org.litote.kmongo.textIndex
import kotlin.reflect.KProperty1

class MainScreenViewModel(viewModelScope: CoroutineScope, client: CoroutineClient) : ViewModel(viewModelScope) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

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

    val collectionClasses = listOf(MockUser::class, MockData::class)

    private val _selectedCollectionClass = MutableStateFlow(collectionClasses.first())
    val selectedCollectionClass = _selectedCollectionClass.asStateFlow()

    private val userCollectionFieldSortOrders = linkedMapOf<KProperty1<MockUser, *>, Boolean>()
    private val userCollectionFields = listOf(MockUser::name, MockUser::age)

    private val dataCollectionFieldSortOrders = linkedMapOf<KProperty1<MockData, *>, Boolean>()
    private val dataCollectionFields = listOf(MockData::data1, MockData::data2, MockData::data3)

    private val _selectedField: MutableStateFlow<KProperty1<out Identifiable<*>, *>> =
        MutableStateFlow(userCollectionFields.first())

    private val _fields: MutableStateFlow<List<KProperty1<out Identifiable<*>, *>>> =
        MutableStateFlow(userCollectionFields)
    val fields = _fields.asStateFlow()

    private val _tableRows = MutableStateFlow(listOf<Identifiable<*>>())
    val tableRows = _tableRows.asStateFlow()

    private suspend fun beforeUpdateTableRows(action: suspend () -> Unit) =
        action().apply { updateTableRows() }

    private suspend fun updateTableRows() {
        suspend fun <T : Identifiable<T>> actualUpdate(
            collection: CoroutineCollection<T>,
            collectionFields: List<KProperty1<T, *>>,
            collectionFieldSortOrders: LinkedHashMap<KProperty1<T, *>, Boolean>,
        ) {
            _tableRows.emit(combineFindSort(collection, collectionFieldSortOrders))
            _fields.emit(collectionFields)
        }

        when (selectedCollectionClass.value) {
            MockUser::class -> actualUpdate(userCollection, userCollectionFields, userCollectionFieldSortOrders)
            MockData::class -> actualUpdate(dataCollection, dataCollectionFields, dataCollectionFieldSortOrders)
            else -> throw IllegalStateException("Wrong selected collection name")
        }
    }

    suspend fun selectCollection(name: String) = beforeUpdateTableRows {
        _selectedCollectionClass.emit(
            requireNotNull(collectionClasses.find { it.simpleName == name }) { "Wrong collection name" }
        )
    }

    suspend fun selectField(name: String) {
        _selectedField.emit(
            value = userCollectionFields.find { it.name == name }
                ?: dataCollectionFields.find { it.name == name }
                ?: throw IllegalArgumentException("Wrong field name")
        )
    }

    suspend fun insert(item: Identifiable<*>) = beforeUpdateTableRows {
        when (item) {
            is MockUser -> userCollection.save(item)
            is MockData -> dataCollection.save(item)
        }
    }

    suspend fun update(item: Identifiable<*>) = beforeUpdateTableRows {
        when (item) {
            is MockUser -> userCollection.updateOne(item)
            is MockData -> dataCollection.updateOne(item)
        }
    }

    suspend fun delete(item: Identifiable<*>) = beforeUpdateTableRows {
        when (item) {
            is MockUser -> userCollection.deleteOneById(requireNotNull(item.id))
            is MockData -> dataCollection.deleteOneById(requireNotNull(item.id))
        }
    }

    suspend fun sortByField(fieldName: String, searchText: String) {
        logger.debug { "Requested field name: $fieldName" }

        when (selectedCollectionClass.value) {
            MockUser::class -> manageFieldSortOrder(
                fieldName = fieldName,
                fieldSortOrders = userCollectionFieldSortOrders,
                property = when (fieldName) {
                    MockUser::name.name -> MockUser::name
                    MockUser::age.name -> MockUser::age
                    else -> throw IllegalArgumentException("Wrong field name passed.")
                },
            )
            MockData::class -> manageFieldSortOrder(
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

        logger.debug { "Sort order for User: $userCollectionFieldSortOrders" }
        logger.debug { "Sort order for Data: $dataCollectionFieldSortOrders" }

        search(searchText)
    }

    suspend fun search(searchText: String) {
        if (searchText.isNotBlank()) {
            val regex = ".*${searchText.trim().asSequence().map(Char::escapeRegex).joinToString(separator = "")}.*"
            logger.debug { "Searching by regex \"$regex\"" }

            _tableRows.emit(
                when (selectedCollectionClass.value) {
                    MockUser::class -> combineFindSort(
                        userCollection,
                        userCollectionFieldSortOrders,
                        when (_selectedField.value) {
                            MockUser::name -> MockUser::name regex regex
                            MockUser::age -> MockUser::age regex regex
                            else -> throw IllegalStateException("Wrong selected field name")
                        }
                    )
                    MockData::class -> combineFindSort(
                        dataCollection,
                        dataCollectionFieldSortOrders,
                        when (_selectedField.value) {
                            MockData::data1 -> MockData::data1 regex regex
                            MockData::data2 -> MockData::data2 regex regex
                            MockData::data3 -> MockData::data3 regex regex
                            else -> throw IllegalStateException("Wrong selected field name")
                        }
                    )
                    else -> throw IllegalStateException("Wrong selected collection name")
                }
            )
        } else updateTableRows()
    }
}
