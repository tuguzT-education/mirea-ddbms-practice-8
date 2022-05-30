package io.github.tuguzt.ddbms.practice8.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id

@Serializable
class Room(
    val floor: Int,
    @Contextual val hotelId: Id<Hotel>,
    val number: Int,
    val description: String,
    val guestCountMax: Int,
    @Contextual @SerialName("_id") override val id: Id<Room>? = null,
) : Identifiable<Room>()
