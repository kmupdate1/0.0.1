package jp.wataju.model.table

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import java.util.UUID

object User: Table("user") {
    val userId:       Column<UUID>    = uuid("user_id").autoGenerate()
    val accountId:    Column<UUID>    = uuid("account_id") references Account.accountId
    val userName:     Column<String>  = varchar("user_name", 50)
    val userNameKana: Column<String?> = varchar("user_name_kana", 50).nullable()
    val userPhone:    Column<String?> = varchar("user_phone", 30).nullable()
    val userMail:     Column<String?> = varchar("user_mail", 100).nullable()

    override val primaryKey = PrimaryKey(userId)
}
