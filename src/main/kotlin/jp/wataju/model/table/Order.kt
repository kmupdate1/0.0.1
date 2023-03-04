package jp.wataju.model.table

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import java.util.*

object Order: Table("order") {
    val orderId:    Column<UUID>   = uuid("order_id").autoGenerate()
    val customerId: Column<UUID>   = uuid("customer_id") references Customer.customerId
    val products:   Column<String> = largeText("products")
    val orderDate:  Column<String> = varchar("order_date", 100)

    override val primaryKey = PrimaryKey(orderId)
}
