package jp.wataju.model.parser

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class OrderProducts(
    @SerialName("product_id") val productId: String,
    @SerialName("amount") val amount: Int
)
