package jp.wataju.model.entity

import jp.wataju.model.table.Account
import org.jetbrains.exposed.sql.ResultRow

class Account(resultRow: ResultRow) {
    val accountId = resultRow[Account.accountId]
    val identify  = resultRow[Account.identify]
    val password  = resultRow[Account.password]
    val admin     = resultRow[Account.admin]
}
