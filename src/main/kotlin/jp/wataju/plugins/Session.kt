package jp.wataju.plugins

import jp.wataju.session.AccountSession
import io.ktor.server.application.*
import io.ktor.server.sessions.*

fun Application.configureSession() {
    install(Sessions) {
        cookie<AccountSession>("user_session")
    }
}
