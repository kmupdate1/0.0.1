package jp.wataju.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import jp.wataju.session.AccountSession

fun Application.configureRouting() {

    routing {

        // ドメインアクセス時のリダイレクト
        get(ROOT_PATH) {
            call.respondRedirect("$CUSTOMER_MANAGER$UNAUTHENTICATED/login")
        }

        // ログアウト後のブラウザバック対応
        get("$CUSTOMER_MANAGER$AUTHENTICATED/login") {
            call.sessions.set(AccountSession(null, null))
            call.respondRedirect("$CUSTOMER_MANAGER$UNAUTHENTICATED/login")
        }

        // ログアウト押下
        get("/logout") {
            call.sessions.set(AccountSession(null, null))
            call.respondRedirect("$CUSTOMER_MANAGER$UNAUTHENTICATED/login")
        }

    }

}

const val ROOT_PATH = "/"
const val CUSTOMER_MANAGER = "/customer-manager"
const val UNAUTHENTICATED = "/unauthenticated"
const val AUTHENTICATED = "/authenticated"
const val CUSTOMER = "/customer"
const val PRODUCT = "/product"
const val SETTING = "/setting"
