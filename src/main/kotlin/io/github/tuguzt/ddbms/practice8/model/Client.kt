package io.github.tuguzt.ddbms.practice8.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id

@Serializable
class Client(
    val sex: String,
    val age: Int,
    val name: String,
    val surname: String,
    @Contextual val bookingId: Id<Booking>,
    @Contextual @SerialName("_id") override val id: Id<Client>? = null,
) : Identifiable<Client>()
