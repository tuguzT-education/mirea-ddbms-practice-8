package io.github.tuguzt.ddbms.practice8.viewmodel

import org.bson.conversions.Bson
import org.litote.kmongo.EMPTY_BSON
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.orderBy
import kotlin.reflect.KProperty1

suspend fun <T : Any> combineFindSort(
    collection: CoroutineCollection<T>,
    collectionFieldSortOrders: LinkedHashMap<KProperty1<T, *>, Boolean>,
    filter: Bson = EMPTY_BSON,
): List<T> =
    collection
        .find(filter)
        .sort(orderBy(collectionFieldSortOrders))
        .toList()

fun <T> manageFieldSortOrder(
    fieldName: String,
    property: KProperty1<T, *>,
    fieldSortOrders: LinkedHashMap<KProperty1<T, *>, Boolean>,
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
