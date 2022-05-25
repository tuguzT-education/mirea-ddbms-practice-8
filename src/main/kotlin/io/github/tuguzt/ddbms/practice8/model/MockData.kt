package io.github.tuguzt.ddbms.practice8.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id

@Serializable
data class MockData(
    val data1: Int,
    val data2: String,
    val data3: Long,
    @Contextual @SerialName("_id") override val id: Id<MockData>? = null,
) : Identifiable<MockData>()

fun MockData.toTableRow(): List<String> = listOf("$data1", data2, "$data3")
