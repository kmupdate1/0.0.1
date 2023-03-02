package jp.wataju

import io.ktor.network.tls.certificates.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import jp.wataju.plugins.configureRouting
import jp.wataju.plugins.configureSession
import jp.wataju.plugins.configureTemplating
import org.slf4j.LoggerFactory
import java.io.File

fun main() {
    val keyStoreFile = File("build/keystore.jks")
    val keyStore = buildKeyStore {
        certificate("watajuAlias") {
            password = "suwannaphum"
            domains  = listOf("127.0.0.1", "0.0.0.0", "localhost")
        }
    }
    keyStore.saveToFile(keyStoreFile, "123456")

    val environment = applicationEngineEnvironment {
        log = LoggerFactory.getLogger("ktor.application")
        connector {
            port = 8080
        }
        sslConnector(
            keyStore = keyStore,
            keyAlias = "watajuAlias",
            keyStorePassword = { "123456".toCharArray() },
            privateKeyPassword = { "suwannaphum".toCharArray() }
        ) {
            port = 8443
            keyStorePath = keyStoreFile
        }
        module(Application::module)
    }

    embeddedServer(Netty, environment).start(wait = true)
}

fun Application.module() {
    configureTemplating()
    configureRouting()
    configureSession()
}
