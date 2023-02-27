package jp.wataju.plugins

import jp.wataju.session.UserSession
import io.ktor.server.application.*
import io.ktor.server.sessions.*

fun Application.configureSession() {
    install(Sessions) {
        cookie<UserSession>("user_session")
    }
}
