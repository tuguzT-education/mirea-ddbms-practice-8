package io.github.tuguzt.ddbms.practice8.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id

@Serializable
data class MockUser(
    val name: String,
    val age: Int,
    @Contextual @SerialName("_id") override val id: Id<MockUser>? = null,
) : Identifiable<MockUser>()
