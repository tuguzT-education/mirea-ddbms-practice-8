package io.github.tuguzt.ddbms.practice8.model

import kotlinx.serialization.Serializable

@Serializable
data class MockUser(val name: String, val age: Int)
