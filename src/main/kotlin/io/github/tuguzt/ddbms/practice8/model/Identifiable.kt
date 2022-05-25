package io.github.tuguzt.ddbms.practice8.model

import org.litote.kmongo.Id

sealed class Identifiable<Self : Identifiable<Self>> {
    abstract val id: Id<Self>?
}
