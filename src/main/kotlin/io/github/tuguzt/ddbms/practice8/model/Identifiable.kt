package io.github.tuguzt.ddbms.practice8.model

import org.litote.kmongo.Id

// todo "specify at least 5 data classes for each table"
sealed class Identifiable<Self : Identifiable<Self>> {
    abstract val id: Id<Self>?
}
