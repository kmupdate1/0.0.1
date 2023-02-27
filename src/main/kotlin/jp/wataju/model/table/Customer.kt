package jp.wataju.model.table

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import java.util.UUID

object Customer: Table("customer") {
    val customerId:       Column<UUID>    = uuid("customer_id").autoGenerate()
    val customerName:     Column<String>  = varchar("customer_name", 100)
    val customerNameKana: Column<String>  = varchar("customer_name_kana", 100)
    val zipcode:          Column<String>  = varchar("zipcode", 100)
    val prefecture:       Column<String>  = varchar("prefecture", 100)
    val address1:         Column<String>  = varchar("address_1", 100)
    val address2:         Column<String>  = varchar("address_2", 100)
    val address3:         Column<String?> = varchar("address_3", 100).nullable()
    val customerPhone:    Column<String?> = varchar("customer_phone", 100).nullable()
    val customerMail:     Column<String?> = varchar("customer_mail", 100).nullable()

    override val primaryKey = PrimaryKey(customerId)
}
