package io.github.tuguzt.ddbms.practice8.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id

@Serializable
class Booking(
    @Contextual val roomId: Id<Room>,
    val guestCount: Int,
    val description: String,
    @Contextual @SerialName("_id") override val id: Id<Booking>? = null,
) : Identifiable<Booking>()
