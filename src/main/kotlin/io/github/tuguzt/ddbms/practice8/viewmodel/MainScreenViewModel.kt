package io.github.tuguzt.ddbms.practice8.viewmodel

import io.github.tuguzt.ddbms.practice8.escapeRegex
import io.github.tuguzt.ddbms.practice8.model.*
import io.github.tuguzt.ddbms.practice8.regex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mu.KotlinLogging
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

    private val hotelChainCollection = database.getCollection<HotelChain>()
    private val hotelCollection = database.getCollection<Hotel>()
    private val roomCollection = database.getCollection<Room>()
    private val bookingCollection = database.getCollection<Booking>()
    private val clientCollection = database.getCollection<Client>()

    val collectionClasses = listOf(
        HotelChain::class,
        Hotel::class,
        Room::class,
        Booking::class,
        Client::class,
    )

    private val _selectedCollectionClass = MutableStateFlow(collectionClasses.first())
    val selectedCollectionClass = _selectedCollectionClass.asStateFlow()

    private val hotelChainFieldSortOrders = linkedMapOf<KProperty1<HotelChain, *>, Boolean>()
    private val hotelChainFields = listOf(HotelChain::description, HotelChain::name)

    private val hotelFieldSortOrders = linkedMapOf<KProperty1<Hotel, *>, Boolean>()
    private val hotelFields = listOf(Hotel::description, Hotel::floorMax, Hotel::name)

    private val roomFieldSortOrders = linkedMapOf<KProperty1<Room, *>, Boolean>()
    private val roomFields = listOf(Room::description, Room::floor, Room::guestCountMax, Room::number)

    private val bookingFieldSortOrders = linkedMapOf<KProperty1<Booking, *>, Boolean>()
    private val bookingFields = listOf(Booking::description, Booking::guestCount)

    private val clientFieldSortOrders = linkedMapOf<KProperty1<Client, *>, Boolean>()
    private val clientFields = listOf(Client::age, Client::name, Client::sex, Client::surname)

    init {
        viewModelScope.launch {
            hotelChainFields.forEach { hotelChainCollection.createIndex(it.textIndex()) }
            hotelFields.forEach { hotelCollection.createIndex(it.textIndex()) }
            roomFields.forEach { roomCollection.createIndex(it.textIndex()) }
            bookingFields.forEach { bookingCollection.createIndex(it.textIndex()) }
            clientFields.forEach { clientCollection.createIndex(it.textIndex()) }
        }
    }

    private val _selectedField = MutableStateFlow(hotelChainFields.first().name)

    private val _fields: MutableStateFlow<List<KProperty1<out Identifiable<*>, *>>> =
        MutableStateFlow(hotelChainFields)
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
            HotelChain::class -> actualUpdate(hotelChainCollection, hotelChainFields, hotelChainFieldSortOrders,)
            Hotel::class -> actualUpdate(hotelCollection, hotelFields, hotelFieldSortOrders,)
            Room::class -> actualUpdate(roomCollection, roomFields, roomFieldSortOrders,)
            Booking::class -> actualUpdate(bookingCollection, bookingFields, bookingFieldSortOrders,)
            Client::class -> actualUpdate(clientCollection, clientFields, clientFieldSortOrders,)
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
            value = hotelChainFields.find { it.name == name }?.name
                ?: hotelFields.find { it.name == name }?.name
                ?: roomFields.find { it.name == name }?.name
                ?: bookingFields.find { it.name == name }?.name
                ?: clientFields.find { it.name == name }?.name
                ?: throw IllegalArgumentException("Wrong field name")
        )
    }

    suspend fun insert(item: Identifiable<*>) = beforeUpdateTableRows {
        when (item) {
            is HotelChain -> hotelChainCollection.save(item)
            is Hotel -> hotelCollection.save(item)
            is Room -> roomCollection.save(item)
            is Booking -> bookingCollection.save(item)
            is Client -> clientCollection.save(item)
        }
    }

    suspend fun update(item: Identifiable<*>) = beforeUpdateTableRows {
        when (item) {
            is HotelChain -> hotelChainCollection.updateOne(item)
            is Hotel -> hotelCollection.updateOne(item)
            is Room -> roomCollection.updateOne(item)
            is Booking -> bookingCollection.updateOne(item)
            is Client -> clientCollection.updateOne(item)
        }
    }

    suspend fun delete(item: Identifiable<*>) = beforeUpdateTableRows {
        when (item) {
            is HotelChain -> hotelChainCollection.deleteOneById(requireNotNull(item.id))
            is Hotel -> hotelCollection.deleteOneById(requireNotNull(item.id))
            is Room -> roomCollection.deleteOneById(requireNotNull(item.id))
            is Booking -> bookingCollection.deleteOneById(requireNotNull(item.id))
            is Client -> clientCollection.deleteOneById(requireNotNull(item.id))
        }
    }

    suspend fun sortByField(fieldName: String, searchText: String) {
        logger.debug { "Requested field name: $fieldName" }

        when (selectedCollectionClass.value) {
            HotelChain::class -> manageFieldSortOrder(
                fieldName = fieldName,
                fieldSortOrders = hotelChainFieldSortOrders,
                property = HotelChain::class.memberProperties.find { it.name == fieldName }
                    ?: throw IllegalArgumentException("Wrong field name passed.")
            )
            Hotel::class -> manageFieldSortOrder(
                fieldName = fieldName,
                fieldSortOrders = hotelFieldSortOrders,
                property = Hotel::class.memberProperties.find { it.name == fieldName }
                    ?: throw IllegalArgumentException("Wrong field name passed.")
            )
            Room::class -> manageFieldSortOrder(
                fieldName = fieldName,
                fieldSortOrders = roomFieldSortOrders,
                property = Room::class.memberProperties.find { it.name == fieldName }
                    ?: throw IllegalArgumentException("Wrong field name passed.")
            )
            Booking::class -> manageFieldSortOrder(
                fieldName = fieldName,
                fieldSortOrders = bookingFieldSortOrders,
                property = Booking::class.memberProperties.find { it.name == fieldName }
                    ?: throw IllegalArgumentException("Wrong field name passed.")
            )
            Client::class -> manageFieldSortOrder(
                fieldName = fieldName,
                fieldSortOrders = clientFieldSortOrders,
                property = Client::class.memberProperties.find { it.name == fieldName }
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
                    HotelChain::class -> combineFindSort(
                        hotelChainCollection,
                        hotelChainFieldSortOrders,
                        when (_selectedField.value) {
                            HotelChain::name.name -> HotelChain::name regex regex
                            HotelChain::description.name -> HotelChain::description regex regex
                            else -> throw IllegalStateException("Wrong selected field name")
                        }
                    )
                    Hotel::class -> combineFindSort(
                        hotelCollection,
                        hotelFieldSortOrders,
                        when (_selectedField.value) {
                            Hotel::name.name -> Hotel::name regex regex
                            Hotel::floorMax.name -> Hotel::floorMax regex regex
                            Hotel::description.name -> Hotel::description regex regex
                            else -> throw IllegalStateException("Wrong selected field name")
                        }
                    )
                    Room::class -> combineFindSort(
                        roomCollection,
                        roomFieldSortOrders,
                        when (_selectedField.value) {
                            Room::floor.name -> Room::floor regex regex
                            Room::number.name -> Room::number regex regex
                            Room::description.name -> Room::description regex regex
                            Room::guestCountMax.name -> Room::guestCountMax regex regex
                            else -> throw IllegalStateException("Wrong selected field name")
                        }
                    )
                    Booking::class -> combineFindSort(
                        bookingCollection,
                        bookingFieldSortOrders,
                        when (_selectedField.value) {
                            Booking::guestCount.name -> Booking::guestCount regex regex
                            Booking::description.name -> Booking::description regex regex
                            else -> throw IllegalStateException("Wrong selected field name")
                        }
                    )
                    Client::class -> combineFindSort(
                        clientCollection,
                        clientFieldSortOrders,
                        when (_selectedField.value) {
                            Client::sex.name -> Client::sex regex regex
                            Client::age.name -> Client::age regex regex
                            Client::name.name -> Client::name regex regex
                            Client::surname.name -> Client::surname regex regex
                            else -> throw IllegalStateException("Wrong selected field name")
                        }
                    )
                    else -> throw IllegalStateException("Wrong selected collection name")
                }
            )
        } else updateTableRows()
    }
}
