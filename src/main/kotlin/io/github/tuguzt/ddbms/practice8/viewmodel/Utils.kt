package io.github.tuguzt.ddbms.practice8.viewmodel

import io.github.tuguzt.ddbms.practice8.model.MockData
import io.github.tuguzt.ddbms.practice8.model.MockUser
import io.github.tuguzt.ddbms.practice8.model.toTableRow
import org.bson.conversions.Bson
import org.litote.kmongo.EMPTY_BSON
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.orderBy
import kotlin.reflect.KProperty

suspend fun combineFindSort(
    collection: CoroutineCollection<*>,
    collectionFieldSortOrders: LinkedHashMap<KProperty<*>, Boolean>,
    filter: Bson = EMPTY_BSON,
): List<List<String>> =
    collection
        .find(filter)
        .sort(orderBy(collectionFieldSortOrders))
        .toList().map { it.toTableRow() }

fun manageFieldSortOrder(
    fieldName: String,
    property: KProperty<*>,
    fieldSortOrders: LinkedHashMap<KProperty<*>, Boolean>
) {
    when (fieldSortOrders.filter { it.key.name == fieldName }.isEmpty()) {
        true -> fieldSortOrders[property] = true
        false -> {
            if (fieldSortOrders[property] == true)
                fieldSortOrders[property] = false
            else fieldSortOrders.remove(property)
        }
    }
}

fun Any.toTableRow(): List<String> = when (this) {
    is MockUser -> this.toTableRow()
    is MockData -> this.toTableRow()
    else -> throw IllegalStateException("Wrong class definition")
}
