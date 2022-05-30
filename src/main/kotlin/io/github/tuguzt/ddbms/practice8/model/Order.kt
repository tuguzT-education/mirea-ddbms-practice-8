package io.github.tuguzt.ddbms.practice8.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id

@Serializable
data class Order(
    @Contextual val userId: Id<User>,
    val items: List<Item>,
    @Contextual @SerialName("_id") override val id: Id<Order>? = null,
) : Identifiable<Order>() {
    @Serializable
    data class Item(
        @Contextual val productId: Id<Product>,
        val quantity: ULong,
    )
}
