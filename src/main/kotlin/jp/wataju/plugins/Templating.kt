package jp.wataju.plugins

import com.github.mustachejava.DefaultMustacheFactory
import io.ktor.server.application.*
import io.ktor.server.mustache.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import jp.wataju.*
import jp.wataju.debug.printMessage
import jp.wataju.model.table.Account
import jp.wataju.model.table.Customer
import jp.wataju.model.table.Product
import jp.wataju.session.UserSession
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

fun Application.configureTemplating() {

    install(Mustache) {
        mustacheFactory = DefaultMustacheFactory("templates/mustache")
    }

    routing {

        // ログイン
        get("/login") {
            val model = mapOf(
                "tab_title" to TAB_TITLE,
                "title" to TITLE,
                "message" to LOGIN_MESSAGE,
                "message_alert" to true
            )

            call.respond(MustacheContent("sign/login.hbs", model))
        }
        post("/login") {

            // 入力値取得
            val loginParams = call.receiveParameters()
            val identify = loginParams["identify"] ?: ""
            val password = loginParams["password"] ?: ""
            var accountExists = false

            // debug
            printMessage("identify", identify)
            printMessage("password", password)

            // データベース参照
            connect(PLATFORM_WINDOWS, CUSTOMER_MANAGEMENT_SYSTEM)
            transaction {
                val query = Account.select(
                    (Account.identify eq identify) and (Account.password eq password)
                )

                kotlin.runCatching {
                    if (query.count() == 1L) {
                        val receive = query.first()
                        call.sessions.set(
                            UserSession(
                                receive[Account.accountId],
                                receive[Account.identify]
                            )
                        )
                        accountExists = true
                    }
                }
            }

            if (accountExists) {
                call.respondRedirect("/search-condition")
            } else {
                val model = mapOf(
                    "tab_title" to TAB_TITLE,
                    "title" to TITLE,
                    "message" to UNSUCCESSFUL_LOGIN_MESSAGE,
                    "message_alert" to false
                )

                call.respond(MustacheContent("sign/login.hbs", model))
            }
        }

        // サインイン
        get("/signing") {
            val model = mapOf(
                "tab_title" to TAB_TITLE,
                "title" to TITLE,
                "message" to SIGNING_MESSAGE,
                "message_alert" to true
            )

            call.respond(MustacheContent("sign/signing.hbs", model))
        }
        post("/signing") {
            val signingParams = call.receiveParameters()
            val identify = signingParams["identify"] ?: ""
            val password = signingParams["password"] ?: ""
            val passwordConfirm = signingParams["password-confirm"] ?: ""
            val admin = signingParams["admin"] ?: ""

            var userExists = false
            connect(PLATFORM_WINDOWS, CUSTOMER_MANAGEMENT_SYSTEM)
            transaction {
                val query = Account.select { Account.identify eq identify }
                query.forEach {
                    if ( it[Account.identify] == identify ) userExists = true
                }
            }

            if (userExists) {
                val model = mapOf(
                    "tab_title" to TAB_TITLE,
                    "title" to TITLE,
                    "message" to USER_ALREADY_EXISTS,
                    "message_alert" to false
                )

                call.respond(MustacheContent("sign/signing.hbs", model))
            } else {
                if ((password != passwordConfirm) or (password == "")) {
                    val model = mapOf(
                        "tab_title" to TAB_TITLE,
                        "title" to TITLE,
                        "message" to UNSUCCESSFUL_SIGNING_MESSAGE,
                        "message_alert" to false
                    )

                    call.respond(MustacheContent("sign/signing.hbs", model))
                } else {
                    // データベース登録
                    var adminFlag = false
                    if (admin == "on") adminFlag = true
                    connect(PLATFORM_WINDOWS, CUSTOMER_MANAGEMENT_SYSTEM)
                    transaction {
                        Account.insert {
                            it[Account.identify] = identify
                            it[Account.password] = password
                            it[Account.admin] = adminFlag
                        }
                    }

                    // ログイン画面へリダイレクト
                    call.respondRedirect("/login")
                }
            }
        }

        // 検索条件
        get("/search-condition") {
            val session = call.sessions.get() ?: UserSession(null, null)

            val model = mapOf(
                "tab_title" to TAB_TITLE,
                "title" to TITLE,
                "identify" to session.identify
            )

            call.respond(MustacheContent("top/top.hbs", model))
        }

        // お客様情報
        get("/customer-list") {
            val session = call.sessions.get() ?: UserSession(null, null)

            val customers = mutableListOf<jp.wataju.model.entity.Customer>()
            connect(PLATFORM_WINDOWS, CUSTOMER_MANAGEMENT_SYSTEM)
            transaction {
                Customer.selectAll().limit(10)
                    .forEach {
                        customers.add(jp.wataju.model.entity.Customer(it))
                    }
            }

            val model = mapOf(
                "tab_title" to TAB_TITLE,
                "title" to TITLE,
                "identify" to session.identify,
                "page_message" to CUSTOMER_INFORMATION_LIST,
                "customers" to customers,
                "customer_detail" to false
            )

            call.respond(MustacheContent("customer/customer.hbs", model))
        }
        get("/customer-detail-7cee08fe-5fa0-456b-b969-77aae6f4481d") {
            val session = call.sessions.get() ?: UserSession(null, null)

            var customer: jp.wataju.model.entity.Customer? = null
            connect(PLATFORM_WINDOWS, CUSTOMER_MANAGEMENT_SYSTEM)
            transaction {
                val query = Customer.select { Customer.customerId eq UUID.fromString("7cee08fe-5fa0-456b-b969-77aae6f4481d")}
                kotlin.runCatching {
                    customer = jp.wataju.model.entity.Customer(query.first())
                }
            }

            val model = mapOf(
                "tab_title" to TAB_TITLE,
                "title" to TITLE,
                "identify" to session.identify,
                "page_message" to CUSTOMER_INFORMATION_DETAIL,
                "customer" to customer,
                "customer_detail" to true
            )

            call.respond(MustacheContent("customer/customer.hbs", model))
        }

        // 商品情報
        get("/product-list") {
            val session = call.sessions.get() ?: UserSession(null, null)

            val products = mutableListOf<jp.wataju.model.entity.Product>()
            connect(PLATFORM_WINDOWS, CUSTOMER_MANAGEMENT_SYSTEM)
            transaction {
                Product.selectAll().limit(10)
                    .forEach {
                        products.add(jp.wataju.model.entity.Product(it))
                    }
            }

            val model = mapOf(
                "tab_title" to TAB_TITLE,
                "title" to TITLE,
                "identify" to session.identify,
                "page_message" to PRODUCT_INFORMATION_LIST,
                "products" to products,
                "product_detail" to false
            )

            call.respond(MustacheContent("product/product.hbs", model))
        }
        get("/product-detail-6d1a4380-83d9-404b-9b14-6557a506f3fc") {
            val session = call.sessions.get() ?: UserSession(null, null)

            var product: jp.wataju.model.entity.Product? = null
            connect(PLATFORM_WINDOWS, CUSTOMER_MANAGEMENT_SYSTEM)
            transaction {
                val query = Product.select { Product.productId eq UUID.fromString("6d1a4380-83d9-404b-9b14-6557a506f3fc") }
                kotlin.runCatching {
                    product = jp.wataju.model.entity.Product(query.first())
                }
            }

            val model = mapOf(
                "tab_title" to TAB_TITLE,
                "title" to TITLE,
                "identify" to session.identify,
                "page_message" to PRODUCT_INFORMATION_DETAIL,
                "product" to product,
                "product_detail" to true
            )

            call.respond(MustacheContent("customer/customer.hbs", model))
        }

        // ユーザ設定
        get("/setting") {
            val session = call.sessions.get() ?: UserSession(null, null)

            val model = mapOf(
                "tab_title" to TAB_TITLE,
                "title" to TITLE,
                "identify" to session.identify,
                "flag_confirm" to false
            )

            call.respond(MustacheContent("setting/setting.hbs", model))
        }
        post("/registry-confirm") {
            val session = call.sessions.get() ?: UserSession(null, null)

            val userSettingParams = call.receiveParameters()
            val userName = userSettingParams["user-name"] ?: ""
            val userNameKana = userSettingParams["user-name-kana"] ?: ""
            val userPhone = userSettingParams["user-phone"] ?: ""
            val userMail = userSettingParams["user-mail"] ?: ""

            if (userName != "") {

                val data = arrayOf(
                    mapOf("label" to "ユーザ名", "element" to userName),
                    mapOf("label" to "ユーザ名（カナ）", "element" to userNameKana),
                    mapOf("label" to "電話番号", "element" to userPhone),
                    mapOf("label" to "メールアドレス", "element" to userMail)
                )

                val model = mapOf(
                    "tab_title" to TAB_TITLE,
                    "title" to TITLE,
                    "identify" to session.identify,
                    "user_data" to data,
                    "flag_confirm" to true,
                )

                call.respond(MustacheContent("setting/setting.hbs", model))
            } else {
                val model = mapOf(
                    "tab_title" to TAB_TITLE,
                    "title" to TITLE,
                    "identify" to session.identify,
                    "message" to UNSUCCESSFUL_REGISTRY_USER,
                    "message_alert" to false,
                    "flag_confirm" to false
                )

                call.respond(MustacheContent("setting/setting.hbs", model))
            }

        }

    }

}
