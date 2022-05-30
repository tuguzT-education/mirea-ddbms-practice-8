package io.github.tuguzt.ddbms.practice8.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id

@Serializable
class Hotel(
    val name: String,
    val floorMax: Int,
    @Contextual val hotelChainId: Id<HotelChain>,
    val description: String,
    @Contextual @SerialName("_id") override val id: Id<Hotel>? = null,
) : Identifiable<Hotel>()
