package jp.wataju.plugins

import com.github.mustachejava.DefaultMustacheFactory
import com.google.gson.Gson
import io.ktor.server.application.*
import io.ktor.server.mustache.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import jp.wataju.*
import jp.wataju.model.table.*
import jp.wataju.pool.*
import jp.wataju.session.AccountSession
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun Application.configureTemplating() {

    install(Mustache) {
        mustacheFactory = DefaultMustacheFactory("templates/mustache")
    }

    routing {

        route(WATAJU) {

            route(CUSTOMER_MANAGER) {

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

                    // データベース参照
                    connect(DATA_PATH, CUSTOMER_MANAGEMENT_SYSTEM)
                    transaction {
                        val query = Account.select(
                            (Account.identify eq identify) and (Account.password eq password)
                        )

                        kotlin.runCatching {
                            if (query.count() == 1L) {
                                val receive = query.first()
                                call.sessions.set(
                                    AccountSession(
                                        receive[Account.accountId],
                                        receive[Account.identify]
                                    )
                                )
                                accountExists = true
                            }
                        }
                    }

                    if (accountExists) {
                        call.respondRedirect("$WATAJU$CUSTOMER_MANAGER/top")
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
                    connect(DATA_PATH, CUSTOMER_MANAGEMENT_SYSTEM)
                    transaction {
                        Account.select { Account.identify eq identify }
                            .forEach {
                                if (it[Account.identify] == identify) userExists = true
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
                            connect(DATA_PATH, CUSTOMER_MANAGEMENT_SYSTEM)
                            transaction {
                                Account.insert {
                                    it[Account.identify] = identify
                                    it[Account.password] = password
                                    it[Account.admin] = adminFlag
                                }
                            }

                            // ログイン画面へリダイレクト
                            call.respondRedirect(REDIRECT)
                        }
                    }
                }

                // 検索条件
                get("/top") {
                    val session = call.sessions.get() ?: AccountSession(null, null)

                    if (session.accountId != null) {
                        val model = mapOf(
                            "tab_title" to TAB_TITLE,
                            "title" to TITLE,
                            "identify" to session.identify
                        )

                        call.respond(MustacheContent("top/top.hbs", model))
                    } else {
                        call.respondRedirect(REDIRECT)
                    }
                }

                // お客様情報
                route(CUSTOMER) {
                    get("/list") {
                        val session = call.sessions.get() ?: AccountSession(null, null)

                        if (session.accountId != null) {
                            val customers = mutableListOf<jp.wataju.model.entity.Customer>()
                            connect(DATA_PATH, CUSTOMER_MANAGEMENT_SYSTEM)
                            transaction {
                                Customer.selectAll()
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
                        } else {
                            call.respondRedirect("$WATAJU$CUSTOMER_MANAGER/login")
                        }
                    }
                    get("/{customer_id}") {
                        val session = call.sessions.get() ?: AccountSession(null, null)
                        val customerId = call.parameters["customer_id"]

                        if (session.accountId != null) {
                            var customer: jp.wataju.model.entity.Customer? = null
                            connect(DATA_PATH, CUSTOMER_MANAGEMENT_SYSTEM)
                            transaction {
                                val query = Customer.select { Customer.customerId eq UUID.fromString(customerId) }
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
                                "customer_detail" to true,
                                "flag_add" to false
                            )

                            call.respond(MustacheContent("customer/customer.hbs", model))
                        } else {
                            call.respondRedirect(REDIRECT)
                        }
                    }
                    get("/add") {
                        val session = call.sessions.get() ?: AccountSession(null, null)

                        if (session.accountId != null) {
                            val model = mapOf(
                                "tab_title" to TAB_TITLE,
                                "title" to TITLE,
                                "identify" to session.identify
                            )

                            call.respond(MustacheContent("customer/customer_edit.hbs", model))
                        } else {
                            call.respondRedirect(REDIRECT)
                        }
                    }
                    post("/add/new") {
                        val session = call.sessions.get() ?: AccountSession(null, null)

                        if (session.accountId != null) {
                            val customerInfoParams = call.receiveParameters()

                            RegistryPool.customerRegistry = CustomerRegistry(
                                customerInfoParams["customer-name"] ?: "",
                                customerInfoParams["customer-name-kana"] ?: "",
                                customerInfoParams["zipcode"] ?: "",
                                customerInfoParams["prefecture"] ?: "",
                                customerInfoParams["address-1"] ?: "",
                                customerInfoParams["address-2"] ?: "",
                                customerInfoParams["address-3"] ?: "",
                                customerInfoParams["customer-phone"] ?: "",
                                customerInfoParams["customer-mail"] ?: ""
                            )

                            val model = mapOf(
                                "tab_title" to TAB_TITLE,
                                "title" to TITLE,
                                "identify" to session.identify,
                                "page_message" to EDIT_AND_REGISTRY,
                                "customer" to RegistryPool.customerRegistry,
                                "customer_detail" to true,
                                "flag_add" to true
                            )

                            call.respond(MustacheContent("customer/customer.hbs", model))
                        } else {
                            call.respondRedirect(REDIRECT)
                        }
                    }
                    get("/registry") {
                        val session = call.sessions.get() ?: AccountSession(null, null)

                        if (session.accountId != null) {
                            val customerPool = RegistryPool.customerRegistry
                            connect(DATA_PATH, CUSTOMER_MANAGEMENT_SYSTEM)
                            transaction {
                                Customer.insert {
                                    it[customerName] = customerPool.customerName
                                    it[customerNameKana] = customerPool.customerNameKana
                                    it[zipcode] = customerPool.zipcode
                                    it[prefecture] = customerPool.prefecture
                                    it[address1] = customerPool.address1
                                    it[address2] = customerPool.address2
                                    it[address3] = customerPool.address3
                                    it[customerPhone] = customerPool.customerPhone
                                    it[customerMail] = customerPool.customerMail
                                }
                            }

                            call.respondRedirect("$WATAJU$CUSTOMER_MANAGER/top")
                        } else {
                            call.respondRedirect(REDIRECT)
                        }
                    }
                }

                // 商品情報
                route(PRODUCT) {
                    get("/list") {
                        val session = call.sessions.get() ?: AccountSession(null, null)

                        if (session.accountId != null) {
                            val products = mutableListOf<jp.wataju.model.entity.Product>()
                            connect(DATA_PATH, CUSTOMER_MANAGEMENT_SYSTEM)
                            transaction {
                                Product.selectAll()
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
                        } else {
                            call.respondRedirect(REDIRECT)
                        }
                    }
                    get("/{product_id}") {
                        val session = call.sessions.get() ?: AccountSession(null, null)
                        val productId = call.parameters["product_id"]

                        if (session.accountId != null) {
                            var product: jp.wataju.model.entity.Product? = null
                            connect(DATA_PATH, CUSTOMER_MANAGEMENT_SYSTEM)
                            transaction {
                                val query = Product.select { Product.productId eq UUID.fromString(productId) }
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

                            call.respond(MustacheContent("product/product.hbs", model))
                        } else {
                            call.respondRedirect(REDIRECT)
                        }
                    }
                }

                // 注文状況
                route(ORDER) {

                    // お客様
                    get("/{customer_id}") {
                        val session = call.sessions.get() ?: AccountSession(null, null)

                        if ( session.accountId != null) {

                            val customerId = call.parameters["customer_id"]
                            var customer: jp.wataju.model.entity.Customer? = null

                            SearchPool.orders.clear()
                            connect(DATA_PATH, CUSTOMER_MANAGEMENT_SYSTEM)
                            transaction {
                                val query = Customer.select { Customer.customerId eq UUID.fromString(customerId) }
                                customer = jp.wataju.model.entity.Customer(query.first())

                                Order.selectAll().forEach {
                                    SearchPool.orders.add(jp.wataju.model.entity.Order(it))
                                }
                            }

                            val model = mapOf(
                                "tab_title" to TAB_TITLE,
                                "title" to TITLE,
                                "identify" to session.identify,
                                "page_message" to DISPLAY_ORDER_BY_CUSTOMER,
                                "customer" to customer,
                                "data_list" to SearchPool.orders,
                                "flag_list" to true
                            )

                            call.respond(MustacheContent("order/order.hbs", model))
                        } else {
                            call.respondRedirect(REDIRECT)
                        }
                    }
                    get("/{customer_id}/{order_id}") {
                        val session = call.sessions.get() ?: AccountSession(null, null)

                        if ( session.accountId != null) {

                            val customerId = UUID.fromString(call.parameters["customer_id"])
                            var customer: jp.wataju.model.entity.Customer? = null
                            connect(DATA_PATH, CUSTOMER_MANAGEMENT_SYSTEM)
                            transaction {
                                val query = Customer.select { Customer.customerId eq customerId }
                                customer = jp.wataju.model.entity.Customer(query.first())
                            }

                            val model = mapOf(
                                "tab_title" to TAB_TITLE,
                                "title" to TITLE,
                                "identify" to session.identify,
                                "page_message" to DISPLAY_ORDER_BY_CUSTOMER,
                                "customer" to customer,
                                "data_list" to SearchPool.orders,
                                "flag_list" to false
                            )
                            call.respond(MustacheContent("order/order.hbs", model))
                        } else {
                            call.respondRedirect(REDIRECT)
                        }
                    }
                    get("/edit/{customer_id}") {
                        val session = call.sessions.get() ?: AccountSession(null, null)

                        val customerId = call.parameters["customer_id"]
                        if (session.accountId != null) {

                            var customer: jp.wataju.model.entity.Customer? = null
                            val products: ArrayList<jp.wataju.model.entity.Product> = arrayListOf()

                            connect(DATA_PATH, CUSTOMER_MANAGEMENT_SYSTEM)
                            transaction {
                                val query = Customer.select { Customer.customerId eq UUID.fromString(customerId) }
                                kotlin.runCatching {
                                    customer = jp.wataju.model.entity.Customer(query.first())
                                }

                                Product.selectAll()
                                    .forEach {
                                        products.add(jp.wataju.model.entity.Product(it))
                                    }
                            }

                            val model = mapOf(
                                "tab_title" to TAB_TITLE,
                                "title" to TITLE,
                                "identify" to session.identify,
                                "page_message" to ADD_ORDER_BY_CUSTOMER,
                                "customer" to customer,
                                "products" to products,
                                "flag_confirm" to false
                            )

                            call.respond(MustacheContent("order/order_customer.hbs", model))
                        } else {
                            call.respondRedirect(REDIRECT)
                        }
                    }
                    post("/confirm/{customer_id}") {
                        val session = call.sessions.get() ?: AccountSession(null, null)

                        val customerId = call.parameters["customer_id"]
                        val orderInfoParams = call.receiveParameters()
                        if (session.accountId != null) {

                            // パラメータが全部空のときは編集画面にリダイレクト
                            if ((orderInfoParams["purchase-date"] ?: "") != "") {

                                // 指定の顧客の購入情報一覧を記憶
                                val orders: MutableMap<UUID, Int> = mutableMapOf()
                                orderInfoParams.forEach { name, values ->
                                    if (name != "purchase-date") {
                                        orders[UUID.fromString(name)] = values[0].toInt()
                                    }
                                }
                                RegistryPool.orderRegistry = OrderRegistry(
                                    UUID.fromString(customerId),
                                    orders,
                                    orderInfoParams["purchase-date"] ?: ""
                                )

                                // 表示用リスト作成
                                var customer: jp.wataju.model.entity.Customer? = null
                                val orderData: ArrayList<Map<String, Any>> = arrayListOf()
                                connect(DATA_PATH, CUSTOMER_MANAGEMENT_SYSTEM)
                                transaction {
                                    val query = Customer.select { Customer.customerId eq UUID.fromString(customerId) }
                                    kotlin.runCatching {
                                        customer = jp.wataju.model.entity.Customer(query.first())
                                    }

                                    orders.forEach {
                                        if (it.value != 0) {
                                            val productQuery = Product.select { Product.productId eq it.key }
                                            orderData.add(
                                                mapOf(
                                                    "product_name" to productQuery.first()[Product.productName],
                                                    "order" to it.value
                                                )
                                            )
                                        }
                                    }
                                }

                                val model = mapOf(
                                    "tab_title" to TAB_TITLE,
                                    "title" to TITLE,
                                    "identify" to session.identify,
                                    "page_message" to EDIT_AND_REGISTRY,
                                    "customer" to customer,
                                    "orders" to orderData,
                                    "order_date" to RegistryPool.orderRegistry.orderDate,
                                    "flag_confirm" to true
                                )

                                call.respond(MustacheContent("order/order_customer.hbs", model))
                            } else {
                                call.respondRedirect("$WATAJU$CUSTOMER_MANAGER$ORDER/edit/$customerId")
                            }
                        } else {
                            call.respondRedirect(REDIRECT)
                        }
                    }
                    get("/registry/{customer_id}") {
                        val session = call.sessions.get() ?: AccountSession(null, null)

                        if ( session.accountId != null) {

                            val orderRegistry = RegistryPool.orderRegistry
                            val customerId =  UUID.fromString(call.parameters["customer_id"])
                            val products = Gson().toJson(orderRegistry.orders)
                            val orderDate = orderRegistry.orderDate

                            // データベース登録
                            connect(DATA_PATH, CUSTOMER_MANAGEMENT_SYSTEM)
                            transaction {
                                Order.insert {
                                    it[Order.customerId] = customerId
                                    it[Order.products] = products
                                    it[Order.orderDate] = orderDate
                                }
                            }

                            call.respondRedirect("$WATAJU$CUSTOMER_MANAGER/top")
                        } else {
                            call.respondRedirect(REDIRECT)
                        }
                    }

                }

                // ユーザ設定
                route(SETTING) {
                    get("/{identify}") {
                        val session = call.sessions.get() ?: AccountSession(null, null)
                        call.parameters["identify"]

                        if (session.accountId != null) {
                            val model = mapOf(
                                "tab_title" to TAB_TITLE,
                                "title" to TITLE,
                                "identify" to session.identify,
                                "flag_confirm" to false
                            )

                            call.respond(MustacheContent("setting/setting.hbs", model))
                        } else {
                            call.respondRedirect(REDIRECT)
                        }
                    }
                    post("/confirm/{identify}") {
                        val session = call.sessions.get() ?: AccountSession(null, null)
                        call.parameters["identify"]

                        if (session.accountId != null) {
                            val userInfoParams = call.receiveParameters()

                            if (userInfoParams["user-name"] != "") {
                                RegistryPool.userRegistry = UserRegistry(
                                    userInfoParams["user-name"] ?: "",
                                    userInfoParams["user-name-kana"] ?: "",
                                    userInfoParams["user-phone"] ?: "",
                                    userInfoParams["user-mail"] ?: ""
                                )

                                val userRegistries = RegistryPool.userRegistry
                                val data = arrayOf(
                                    mapOf("label" to "ユーザ名", "element" to userRegistries.userName),
                                    mapOf("label" to "ユーザ名（カナ）", "element" to userRegistries.userNameKana),
                                    mapOf("label" to "電話番号", "element" to userRegistries.userPhone),
                                    mapOf("label" to "メールアドレス", "element" to userRegistries.userMail)
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
                        } else {
                            call.respondRedirect(REDIRECT)
                        }

                    }
                    get("/registry/{identify}") {
                        val session = call.sessions.get() ?: AccountSession(null, null)
                        call.parameters["identify"]

                        if (session.accountId != null) {
                            var user: jp.wataju.model.entity.User? = null
                            connect(DATA_PATH, CUSTOMER_MANAGEMENT_SYSTEM)
                            transaction {
                                val query = User.select { User.accountId eq session.accountId!! }
                                kotlin.runCatching {
                                    user = jp.wataju.model.entity.User(query.first())
                                }
                            }

                            val userPool = RegistryPool.userRegistry
                            if (user?.accountId != session.accountId) {

                                connect(DATA_PATH, CUSTOMER_MANAGEMENT_SYSTEM)
                                transaction {
                                    User.insert {
                                        it[accountId] = session.accountId!!
                                        it[userName] = userPool.userName
                                        it[userNameKana] = userPool.userNameKana
                                        it[userPhone] = userPool.userPhone
                                        it[userMail] = userPool.userMail
                                    }
                                }

                                call.respondRedirect("$WATAJU$CUSTOMER_MANAGER/top")
                            } else {
                                val data = arrayOf(
                                    mapOf("label" to "ユーザ名", "element" to userPool.userName),
                                    mapOf("label" to "ユーザ名（カナ）", "element" to userPool.userNameKana),
                                    mapOf("label" to "電話番号", "element" to userPool.userPhone),
                                    mapOf("label" to "メールアドレス", "element" to userPool.userMail)
                                )

                                val model = mapOf(
                                    "tab_title" to TAB_TITLE,
                                    "title" to TITLE,
                                    "identify" to session.identify,
                                    "message_alert" to false,
                                    "message" to ALREADY_REGISTRY_USER,
                                    "user_data" to data,
                                    "flag_confirm" to true,
                                )

                                call.respond(MustacheContent("setting/setting.hbs", model))
                            }
                        } else {
                            call.respondRedirect(REDIRECT)
                        }
                    }
                }

            }

        }

    }
}
