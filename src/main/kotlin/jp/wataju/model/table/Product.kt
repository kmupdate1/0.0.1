package jp.wataju.model.table

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import java.util.UUID

object Product: Table("product") {
    val productId:   Column<UUID>   = uuid("product_id").autoGenerate()
    val productName: Column<String> = varchar("product_name", 100)
    val price:       Column<Int>    = integer("price")

    override val primaryKey = PrimaryKey(productId)
}
