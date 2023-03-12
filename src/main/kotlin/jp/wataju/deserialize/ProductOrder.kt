package jp.wataju.deserialize

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ProductOrder(
    @SerialName("product_id")
    val productId: String,

    @SerialName("number")
    val number: Int
)
