package jp.wataju.plugins

import jp.wataju.session.UserSession
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*

fun Application.configureRouting() {
    routing {

        get(ROOT_PATH) {
            call.respondRedirect("$CUSTOMER_MANAGER$UNAUTHENTICATED/login")
        }

        route(CUSTOMER_MANAGER) {
            route(UNAUTHENTICATED) {
                get("/logout") {
                    call.sessions.set(UserSession(null, null))
                    call.respondRedirect("$CUSTOMER_MANAGER$UNAUTHENTICATED/login")
                }
            }
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
