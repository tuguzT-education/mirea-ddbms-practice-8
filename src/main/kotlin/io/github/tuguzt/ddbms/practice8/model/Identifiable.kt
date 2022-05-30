package io.github.tuguzt.ddbms.practice8.model

import kotlinx.serialization.Serializable
import org.litote.kmongo.Id

@Serializable
sealed class Identifiable<Self : Identifiable<Self>> {
    abstract val id: Id<Self>?
}
