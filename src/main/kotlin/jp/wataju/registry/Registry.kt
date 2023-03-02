package jp.wataju.registry

data class UserRegistry(
    val userName: String,
    val userNameKana: String,
    val userPhone: String,
    val userMail: String
)

data class CustomerRegistry (
    var customerName: String,
    var customerNameKana: String,
    var zipcode: String,
    var prefecture: String,
    var address1: String,
    var address2: String,
    var address3: String,
    var customerPhone: String,
    var customerMail: String
)
