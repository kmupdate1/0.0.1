package jp.wataju.debug

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


@Serializable
data class Fruit(
    @SerialName("name") val name: String,
    @SerialName("value") val value: Int,
    @SerialName("amount") val amount: Int
)

fun main() {
    val json = Json.encodeToString(
        listOf(
            Fruit("apple", 100, 1),
            Fruit("banana", 500, 3)
        )
    )

    println(json)
    println(Json.decodeFromString(ListSerializer(Fruit.serializer()), json))
}
