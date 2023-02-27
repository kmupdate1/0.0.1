package jp.wataju.model.entity

import jp.wataju.model.table.Customer
import org.jetbrains.exposed.sql.ResultRow

class Customer(resultRow: ResultRow) {
    val customerId = resultRow[Customer.customerId]
    val customerName = resultRow[Customer.customerName]
    val customerNameKana = resultRow[Customer.customerNameKana]
    val zipcode = resultRow[Customer.zipcode]
    val prefecture = resultRow[Customer.prefecture]
    val address1 = resultRow[Customer.address1]
    val address2 = resultRow[Customer.address2]
    val address3 = resultRow[Customer.address3]
    val customerPhone = resultRow[Customer.customerPhone]
    val customerMail = resultRow[Customer.customerMail]
}
