package jp.wataju.model.table

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import java.util.UUID

object Account: Table("account") {
    val accountId: Column<UUID>    = uuid("account_id").autoGenerate()
    val identify:  Column<String>  = varchar("identify", 100)
    val password:  Column<String>  = varchar("password", 100)
    val admin:     Column<Boolean> = bool("admin")

    override val primaryKey = PrimaryKey(accountId)
}
