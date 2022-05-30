package io.github.tuguzt.ddbms.practice8.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id

@Serializable
data class ProductCategory(
    val name: String,
    val description: String,
    @Contextual @SerialName("_id") override val id: Id<ProductCategory>? = null,
) : Identifiable<ProductCategory>()
