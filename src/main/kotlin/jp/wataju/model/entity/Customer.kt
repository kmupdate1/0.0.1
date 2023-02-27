package jp.wataju.model.entity

import jp.wataju.model.table.Customer
import org.jetbrains.exposed.sql.ResultRow

class Customer(resultRow: ResultRow) {
    var customerId = resultRow[Customer.customerId]
    var customerName = resultRow[Customer.customerName]
    var customerNameKana = resultRow[Customer.customerNameKana]
    var zipcode = resultRow[Customer.zipcode]
    var prefecture = resultRow[Customer.prefecture]
    var address1 = resultRow[Customer.address1]
    var address2 = resultRow[Customer.address2]
    var address3 = resultRow[Customer.address3]
    var customerPhone = resultRow[Customer.customerPhone]
    var customerMail = resultRow[Customer.customerMail]
}
