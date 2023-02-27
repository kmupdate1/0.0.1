package jp.wataju.model.entity

import jp.wataju.model.table.Account
import org.jetbrains.exposed.sql.ResultRow

class Account(resultRow: ResultRow) {
    var accountId = resultRow[Account.accountId]
    var identify  = resultRow[Account.identify]
    var password  = resultRow[Account.password]
    var admin     = resultRow[Account.admin]
}
