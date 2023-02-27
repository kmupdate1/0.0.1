package jp.wataju.session

import java.util.*

data class UserSession(
    var accountId: UUID?,
    var identify: String?
)
