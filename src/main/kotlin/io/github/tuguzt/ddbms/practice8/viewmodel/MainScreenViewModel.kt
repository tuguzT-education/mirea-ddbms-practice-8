package io.github.tuguzt.ddbms.practice8.viewmodel

import io.github.tuguzt.ddbms.practice8.escapeRegex
import io.github.tuguzt.ddbms.practice8.model.*
import io.github.tuguzt.ddbms.practice8.regex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.litote.kmongo.EMPTY_BSON
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.updateOne
import org.litote.kmongo.regex
import org.litote.kmongo.textIndex
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

class MainScreenViewModel(viewModelScope: CoroutineScope, database: CoroutineDatabase) : ViewModel(viewModelScope) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    private val manufacturerCollection = database.getCollection<Manufacturer>()
    private val orderCollection = database.getCollection<Order>()
    private val productCollection = database.getCollection<Product>()
    private val productCategoryCollection = database.getCollection<ProductCategory>()
    private val userCollection = database.getCollection<User>()

    val collectionClasses = listOf(
        Manufacturer::class,
        Order::class,
        Product::class,
        ProductCategory::class,
        User::class,
    )

    private val _selectedCollectionClass = MutableStateFlow(collectionClasses.first())
    val selectedCollectionClass = _selectedCollectionClass.asStateFlow()

    private val manufacturerFieldSortOrders = linkedMapOf<KProperty1<Manufacturer, *>, Boolean>()
    private val manufacturerFields = listOf(Manufacturer::name, Manufacturer::description)

    private val orderFieldSortOrders = linkedMapOf<KProperty1<Order, *>, Boolean>()
    private val orderFields = listOf(Order::items)

    private val productFieldSortOrders = linkedMapOf<KProperty1<Product, *>, Boolean>()
    private val productFields = listOf(Product::name, Product::description, Product::price, Product::quantity)

    private val productCategoryFieldSortOrders = linkedMapOf<KProperty1<ProductCategory, *>, Boolean>()
    private val productCategoryFields = listOf(ProductCategory::name, ProductCategory::description)

    private val userFieldSortOrders = linkedMapOf<KProperty1<User, *>, Boolean>()
    private val userFields = listOf(User::name, User::surname, User::email, User::phoneNumber)

    init {
        viewModelScope.launch {
            manufacturerFields.forEach { manufacturerCollection.createIndex(it.textIndex()) }
            orderFields.forEach { orderCollection.createIndex(it.textIndex()) }
            productFields.forEach { productCollection.createIndex(it.textIndex()) }
            productCategoryFields.forEach { productCategoryCollection.createIndex(it.textIndex()) }
            userFields.forEach { userCollection.createIndex(it.textIndex()) }
        }
    }

    private val _selectedField = MutableStateFlow(userFields.first().name)

    private val _fields: MutableStateFlow<List<KProperty1<out Identifiable<*>, *>>> =
        MutableStateFlow(userFields)
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
            Manufacturer::class -> actualUpdate(manufacturerCollection, manufacturerFields, manufacturerFieldSortOrders)
            Order::class -> actualUpdate(orderCollection, orderFields, orderFieldSortOrders)
            Product::class -> actualUpdate(productCollection, productFields, productFieldSortOrders)
            ProductCategory::class -> actualUpdate(
                productCategoryCollection,
                productCategoryFields,
                productCategoryFieldSortOrders,
            )
            User::class -> actualUpdate(userCollection, userFields, userFieldSortOrders)
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
            value = manufacturerFields.find { it.name == name }?.name
                ?: orderFields.find { it.name == name }?.name
                ?: productFields.find { it.name == name }?.name
                ?: productCategoryFields.find { it.name == name }?.name
                ?: userFields.find { it.name == name }?.name
                ?: throw IllegalArgumentException("Wrong field name")
        )
    }

    suspend fun insert(item: Identifiable<*>) = beforeUpdateTableRows {
        when (item) {
            is Manufacturer -> manufacturerCollection.save(item)
            is Order -> orderCollection.save(item)
            is Product -> productCollection.save(item)
            is ProductCategory -> productCategoryCollection.save(item)
            is User -> userCollection.save(item)
        }
    }

    suspend fun update(item: Identifiable<*>) = beforeUpdateTableRows {
        when (item) {
            is Manufacturer -> manufacturerCollection.updateOne(item)
            is Order -> orderCollection.updateOne(item)
            is Product -> productCollection.updateOne(item)
            is ProductCategory -> productCategoryCollection.updateOne(item)
            is User -> userCollection.updateOne(item)
        }
    }

    suspend fun delete(item: Identifiable<*>) = beforeUpdateTableRows {
        when (item) {
            is Manufacturer -> manufacturerCollection.deleteOneById(requireNotNull(item.id))
            is Order -> orderCollection.deleteOneById(requireNotNull(item.id))
            is Product -> productCollection.deleteOneById(requireNotNull(item.id))
            is ProductCategory -> productCategoryCollection.deleteOneById(requireNotNull(item.id))
            is User -> userCollection.deleteOneById(requireNotNull(item.id))
        }
    }

    suspend fun sortByField(fieldName: String, searchText: String) {
        logger.debug { "Requested field name: $fieldName" }

        when (selectedCollectionClass.value) {
            Manufacturer::class -> manageFieldSortOrder(
                fieldName = fieldName,
                fieldSortOrders = manufacturerFieldSortOrders,
                property = Manufacturer::class.memberProperties.find { it.name == fieldName }
                    ?: throw IllegalArgumentException("Wrong field name passed.")
            )
            Order::class -> manageFieldSortOrder(
                fieldName = fieldName,
                fieldSortOrders = orderFieldSortOrders,
                property = Order::class.memberProperties.find { it.name == fieldName }
                    ?: throw IllegalArgumentException("Wrong field name passed.")
            )
            Product::class -> manageFieldSortOrder(
                fieldName = fieldName,
                fieldSortOrders = productFieldSortOrders,
                property = Product::class.memberProperties.find { it.name == fieldName }
                    ?: throw IllegalArgumentException("Wrong field name passed.")
            )
            ProductCategory::class -> manageFieldSortOrder(
                fieldName = fieldName,
                fieldSortOrders = productCategoryFieldSortOrders,
                property = ProductCategory::class.memberProperties.find { it.name == fieldName }
                    ?: throw IllegalArgumentException("Wrong field name passed.")
            )
            User::class -> manageFieldSortOrder(
                fieldName = fieldName,
                fieldSortOrders = userFieldSortOrders,
                property = User::class.memberProperties.find { it.name == fieldName }
                    ?: throw IllegalArgumentException("Wrong field name passed.")
            )
        }

        search(searchText)
    }

    suspend fun search(searchText: String) {
        if (searchText.isNotBlank()) {
            val regex = ".*${searchText.trim().asSequence().map(Char::escapeRegex).joinToString(separator = "")}.*"
            logger.debug { "Searching by regex \"$regex\"" }

            _tableRows.emit(
                when (selectedCollectionClass.value) {
                    Manufacturer::class -> combineFindSort(
                        manufacturerCollection,
                        manufacturerFieldSortOrders,
                        when (_selectedField.value) {
                            Manufacturer::name.name -> Manufacturer::name regex regex
                            Manufacturer::description.name -> Manufacturer::description regex regex
                            else -> throw IllegalStateException("Wrong selected field name")
                        }
                    )
                    Order::class -> combineFindSort(
                        orderCollection,
                        orderFieldSortOrders,
                        when (_selectedField.value) {
                            Order::items.name -> EMPTY_BSON
                            else -> throw IllegalStateException("Wrong selected field name")
                        }
                    )
                    Product::class -> combineFindSort(
                        productCollection,
                        productFieldSortOrders,
                        when (_selectedField.value) {
                            Product::name.name -> Product::name regex regex
                            Product::description.name -> Product::description regex regex
                            Product::price.name -> Product::price regex regex
                            Product::quantity.name -> Product::quantity regex regex
                            else -> throw IllegalStateException("Wrong selected field name")
                        }
                    )
                    ProductCategory::class -> combineFindSort(
                        productCategoryCollection,
                        productCategoryFieldSortOrders,
                        when (_selectedField.value) {
                            ProductCategory::name.name -> ProductCategory::name regex regex
                            ProductCategory::description.name -> ProductCategory::description regex regex
                            else -> throw IllegalStateException("Wrong selected field name")
                        }
                    )
                    User::class -> combineFindSort(
                        userCollection,
                        userFieldSortOrders,
                        when (_selectedField.value) {
                            User::name.name -> User::name regex regex
                            User::surname.name -> User::surname regex regex
                            User::email.name -> User::email regex regex
                            User::phoneNumber.name -> User::phoneNumber regex regex
                            else -> throw IllegalStateException("Wrong selected field name")
                        }
                    )
                    else -> throw IllegalStateException("Wrong selected collection name")
                }
            )
        } else updateTableRows()
    }
}
