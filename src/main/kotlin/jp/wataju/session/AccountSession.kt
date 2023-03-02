package jp.wataju.session

import java.util.*

data class AccountSession(
    var accountId: UUID?,
    var identify: String?
)
