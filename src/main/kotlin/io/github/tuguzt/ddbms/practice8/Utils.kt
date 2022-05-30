package io.github.tuguzt.ddbms.practice8

import com.mongodb.client.model.Filters
import org.bson.conversions.Bson
import java.util.Date
import kotlin.reflect.KProperty

fun String.capitalize(): String =
    this.replaceFirstChar { it.uppercase() }

infix fun KProperty<Number>.regex(regex: String): Bson =
    Filters.where("/$regex/.test(this.$name)")

@JvmName("regexDate")
infix fun KProperty<Date>.regex(regex: String): Bson =
    Filters.where("/$regex/.test(this.$name)")

@JvmName("regexULong")
infix fun KProperty<ULong>.regex(regex: String): Bson =
    Filters.where("/$regex/.test(this.$name)")

fun Char.escapeRegex(): String =
    when (this) {
        '*' -> "\\*"
        '(' -> "\\("
        ')' -> "\\)"
        '\\' -> "\\\\"
        '/' -> "\\/"
        '$' -> "\\$"
        '|' -> "\\|"
        else -> toString()
    }
