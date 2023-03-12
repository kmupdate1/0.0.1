package jp.wataju.pool

import java.util.*

data class UserRegistry(
    val userName: String,
    val userNameKana: String,
    val userPhone: String,
    val userMail: String
)

data class CustomerRegistry (
    val customerName: String,
    val customerNameKana: String,
    val zipcode: String,
    val prefecture: String,
    val address1: String,
    val address2: String,
    val address3: String,
    val customerPhone: String,
    val customerMail: String
)

data class OrderRegistry (
    val customerId: UUID,
    val orders: MutableMap<UUID, Int>,
    val orderDate: String
)
