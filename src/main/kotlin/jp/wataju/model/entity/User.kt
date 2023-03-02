package jp.wataju.model.entity

import jp.wataju.model.table.User
import org.jetbrains.exposed.sql.ResultRow

class User(resultRow: ResultRow) {
    val userId = resultRow[User.userId]
    val accountId = resultRow[User.accountId]
    val userName = resultRow[User.userName]
    val userNameKana = resultRow[User.userNameKana]
    val userPhone = resultRow[User.userPhone]
    val userMail = resultRow[User.userMail]
}
