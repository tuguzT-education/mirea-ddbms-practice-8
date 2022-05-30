package io.github.tuguzt.ddbms.practice8.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id

@Serializable
data class Product(
    val name: String,
    val description: String,
    val price: Double,
    val quantity: ULong,
    @Contextual val manufacturerId: Id<Manufacturer>,
    val categoryIds: List<@Contextual Id<ProductCategory>>,
    @Contextual @SerialName("_id") override val id: Id<Product>? = null,
) : Identifiable<Product>()
