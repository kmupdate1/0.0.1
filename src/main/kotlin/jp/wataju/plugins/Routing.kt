package jp.wataju.plugins

import jp.wataju.session.UserSession
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*

fun Application.configureRouting() {
    routing {
        get("/login") {
            call.respondRedirect("/login")
        }

        get("/top") {
            call.respondRedirect("/search-condition")
        }

        get("/logout") {
            call.sessions.set(UserSession(null, null))
            call.respondRedirect("/login")
        }
    }
}
