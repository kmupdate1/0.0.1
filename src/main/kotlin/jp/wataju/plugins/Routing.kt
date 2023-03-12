package jp.wataju.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import jp.wataju.session.AccountSession

fun Application.configureRouting() {

    routing {

        // ドメインアクセス時のリダイレクト
        get("/") {
            call.respondRedirect(REDIRECT_TO_LOGIN)
        }

        // ログアウト
        get("/logout") {
            call.sessions.set(AccountSession(null, null))
            call.respondRedirect(REDIRECT_TO_LOGIN)
        }

    }

}

const val WATAJU = "/wataju"
const val CUSTOMER_MANAGER = "/customer_manager"
const val CUSTOMER = "/customer"
const val PRODUCT = "/product"
const val ORDER = "/order"
const val SETTING = "/setting"
const val REDIRECT_TO_LOGIN = "$WATAJU$CUSTOMER_MANAGER/login"
