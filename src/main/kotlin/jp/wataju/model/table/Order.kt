package jp.wataju.model.table

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import java.util.UUID

object Order: Table("order") {
    val orderId:    Column<UUID>   = uuid("order_id").autoGenerate()
    val customerId: Column<UUID>   = uuid("customer_id") references Customer.customerId
    val productId:  Column<UUID>   = uuid("product_id") references Product.productId
    val orderDate:  Column<String> = varchar("order_date", 100)

    override val primaryKey = PrimaryKey(orderId)
}
