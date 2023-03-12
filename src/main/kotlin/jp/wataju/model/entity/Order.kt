package jp.wataju.model.entity

import jp.wataju.model.table.Order
import org.jetbrains.exposed.sql.ResultRow

class Order(resultRow: ResultRow) {
    val orderId = resultRow[Order.orderId]
    val customerId = resultRow[Order.customerId]
    val products = resultRow[Order.products]
    val orderDate = resultRow[Order.orderDate]
}
