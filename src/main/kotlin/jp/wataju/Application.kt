package jp.wataju

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import jp.wataju.plugins.configureRouting
import jp.wataju.plugins.configureSession
import jp.wataju.plugins.configureTemplating

fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configureTemplating()
    configureRouting()
    configureSession()
}
