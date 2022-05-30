package io.github.tuguzt.ddbms.practice8.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id

@Serializable
data class User(
    val name: String,
    val surname: String,
    val email: String?,
    val phoneNumber: ULong,
    @Contextual @SerialName("_id") override val id: Id<User>? = null,
) : Identifiable<User>()
